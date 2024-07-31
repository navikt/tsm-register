package no.nav.tsm.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import no.nav.tsm.smregister.models.ReceivedSykmelding
import no.nav.tsm.sykmelding.SykmeldingConsumer
import no.nav.tsm.sykmelding.database.RegisterDB
import no.nav.tsm.sykmelding.util.JacksonKafkaDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import java.util.Properties

fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(
            environmentModule(),
            databaseModule(),
            kafkaConsumer(),
        )
    }
}

fun kafkaConsumer(): Module {
    return module {
        single {
            val env = get<Environment>()

            val consumer: KafkaConsumer<String, ReceivedSykmelding?> = KafkaConsumer(Properties().apply {
                putAll(env.kafkaProperties)
                this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JacksonKafkaDeserializer::class.java.name
                this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
                this[ConsumerConfig.GROUP_ID_CONFIG] = "tsm-register"
                this[ConsumerConfig.CLIENT_ID_CONFIG] = env.hostname
                this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
                this[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "true"
            }, StringDeserializer(), JacksonKafkaDeserializer(ReceivedSykmelding::class))

            SykmeldingConsumer(consumer, get(), env.sykmeldingInputTopic)
        }
    }
}

fun databaseModule(): Module {
    return module {
        single {
            Database.connect(get<Environment>().jdbcUrl, driver = "org.postgresql.Driver")
        }
        single { RegisterDB(get()) }
    }
}

fun Application.environmentModule() = module {
    single { configureEnvironment() }
}
