package no.nav.tsm.sykmelding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
    private val partitionOffsets = mutableMapOf<Int, Long>()
    suspend fun start() = coroutineScope {
        launch(Dispatchers.IO) {
            while (true) {
                delay(60_000)
                partitionOffsets.forEach {
                    (partition, offset) -> logger.info("Partition: $partition, offset: $offset")
                }
            }
        }
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

                var hasNullValue = false
                records.forEach {
                    if (it.value() == null) {
                        hasNullValue = true
                    }
                    partitionOffsets[it.partition()] = it.offset()
                }
                if(hasNullValue) {
                    logger.info("has null values in records, inserting/updating one by one")
                    records.forEach { record ->
                        registerDB.insertOrUpdateSykmelding(record.key(), record.value())
                    }
                } else {
                    registerDB.bachUpsertSykmeldinger(records.map {
                        val receivedSykmelding = it.value()
                        requireNotNull(receivedSykmelding)
                        receivedSykmelding
                    })
                }
            }
        }
    }
}
