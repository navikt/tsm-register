package no.nav.tsm.sykmelding

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import no.nav.tsm.smregister.models.ReceivedSykmelding
import no.nav.tsm.sykmelding.database.RegisterDB
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.coroutines.cancellation.CancellationException

class SykmeldingConsumer(private val kafkaConsumer: KafkaConsumer<String, ReceivedSykmelding?>, private val registerDB: RegisterDB, private val topic: String) {
    companion object {
        private val logger = LoggerFactory.getLogger(SykmeldingConsumer::class.java)
    }
    suspend fun start() = coroutineScope {
        while (isActive) {
            try {
                consumeMessages()
            } catch (e: CancellationException) {
                logger.info("Consumer cancelled")
            } catch (ex: Exception) {
                logger.error("error handling kafka messages", ex)
                kafkaConsumer.unsubscribe()
                delay(60_000)
            }
        }
        kafkaConsumer.unsubscribe()
    }

    private suspend fun consumeMessages()  = coroutineScope {
        kafkaConsumer.subscribe(listOf(topic))
        while (isActive) {
            val records = kafkaConsumer.poll(Duration.ofMillis(10_000))
            if (!records.isEmpty) {
                records.forEach {
                    registerDB.insertOrUpdateSykmelding(it.key(), it.value())
                }
            }
        }
    }
}
