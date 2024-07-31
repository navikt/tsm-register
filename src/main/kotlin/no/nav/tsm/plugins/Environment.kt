package no.nav.tsm.plugins

import io.ktor.server.application.Application
import java.util.Properties
import java.util.UUID

class Environment(
    val jdbcUrl: String,
    val kafkaProperties: Properties,
    val sykmeldingInputTopic: String = "tsm.sykmeldinger-input",
    val hostname: String = System.getenv("NAIS_APP_NAME") ?: "tsm-register-${UUID.randomUUID()}"
)


fun Application.createEnvironment(): Environment {
    return Environment(
        jdbcUrl = environment.config.propertyOrNull("ktor.database.jdbcUrl")?.getString() ?: throw IllegalArgumentException("Missing property: ktor.database.jdbcUrl") ,
        kafkaProperties = Properties().apply {
            environment.config.config("ktor.kafka.config").toMap().forEach {
                this[it.key] = it.value
            }
        },
    )
}

fun Application.configureEnvironment(): Environment {
    return createEnvironment()
}
