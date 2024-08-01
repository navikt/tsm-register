package no.nav.tsm.sykmelding.database

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.tsm.smregister.models.ReceivedSykmelding
import no.nav.tsm.sykmelding.database.tables.Sykmelding
import no.nav.tsm.sykmelding.database.tables.Sykmelding.epjSystemNavn
import no.nav.tsm.sykmelding.database.tables.Sykmelding.epjSystemVersjon
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legeAktoerId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legeFnr
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legeHelsepersonellkategori
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legeHpr
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legekontorHerId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legekontorOrgNr
import no.nav.tsm.sykmelding.database.tables.Sykmelding.legekontorReshId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.merknader
import no.nav.tsm.sykmelding.database.tables.Sykmelding.mottakId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.mottattTidspunkt
import no.nav.tsm.sykmelding.database.tables.Sykmelding.partnerreferansef
import no.nav.tsm.sykmelding.database.tables.Sykmelding.pasientAktoerId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.pasientFnr
import no.nav.tsm.sykmelding.database.tables.Sykmelding.sykmelding
import no.nav.tsm.sykmelding.database.tables.Sykmelding.tssId
import no.nav.tsm.sykmelding.database.tables.Sykmelding.utenlandskSykmelding
import no.nav.tsm.sykmelding.database.tables.Sykmelding.validationResult
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class RegisterDB(private val database: Database) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(RegisterDB::class.java)
    }

    fun insertOrUpdateSykmelding(sykmeldingId: String, receivedSykmelding: ReceivedSykmelding?) {
        transaction(database) {
            if (receivedSykmelding != null) {
                Sykmelding.upsert {
                    it[id] = receivedSykmelding.sykmelding.id
                    it[pasientFnr] = receivedSykmelding.personNrPasient
                    it[pasientAktoerId] = receivedSykmelding.sykmelding.pasientAktoerId
                    it[legeFnr] = receivedSykmelding.personNrLege
                    it[legeAktoerId] = receivedSykmelding.sykmelding.behandler.aktoerId
                    it[mottakId] = receivedSykmelding.navLogId
                    it[legekontorOrgNr] = receivedSykmelding.legekontorOrgNr
                    it[legekontorHerId] = receivedSykmelding.legekontorHerId
                    it[legekontorReshId] = receivedSykmelding.legekontorReshId
                    it[epjSystemNavn] = receivedSykmelding.sykmelding.avsenderSystem.navn
                    it[epjSystemVersjon] = receivedSykmelding.sykmelding.avsenderSystem.versjon
                    it[mottattTidspunkt] = receivedSykmelding.mottattDato
                    it[tssId] = receivedSykmelding.tssid
                    it[merknader] = receivedSykmelding.merknader
                    it[partnerreferansef] = receivedSykmelding.partnerreferanse
                    it[legeHpr] = receivedSykmelding.legeHprNr
                    it[legeHelsepersonellkategori] = receivedSykmelding.legeHelsepersonellkategori
                    it[utenlandskSykmelding] = receivedSykmelding.utenlandskSykmelding
                    it[sykmelding] = receivedSykmelding.sykmelding
                    it[validationResult] = receivedSykmelding.validationResult
                }
            } else {
                val deleted = Sykmelding.deleteWhere { id eq sykmeldingId }
                logger.info("Deleted $deleted rows for sykmeldingId $sykmeldingId")
            }
        }
    }

    fun bachUpsertSykmeldinger(sykmeldinger: List<ReceivedSykmelding>) {
        transaction(database) {
            Sykmelding.batchUpsert(sykmeldinger) { receivedSykmelding ->
                this[Sykmelding.id] = receivedSykmelding.sykmelding.id
                this[pasientFnr] = receivedSykmelding.personNrPasient
                this[pasientAktoerId] = receivedSykmelding.sykmelding.pasientAktoerId
                this[legeFnr] = receivedSykmelding.personNrLege
                this[legeAktoerId] = receivedSykmelding.sykmelding.behandler.aktoerId
                this[mottakId] = receivedSykmelding.navLogId
                this[legekontorOrgNr] = receivedSykmelding.legekontorOrgNr
                this[legekontorHerId] = receivedSykmelding.legekontorHerId
                this[legekontorReshId] = receivedSykmelding.legekontorReshId
                this[epjSystemNavn] = receivedSykmelding.sykmelding.avsenderSystem.navn
                this[epjSystemVersjon] = receivedSykmelding.sykmelding.avsenderSystem.versjon
                this[mottattTidspunkt] = receivedSykmelding.mottattDato
                this[tssId] = receivedSykmelding.tssid
                this[merknader] = receivedSykmelding.merknader
                this[partnerreferansef] = receivedSykmelding.partnerreferanse
                this[legeHpr] = receivedSykmelding.legeHprNr
                this[legeHelsepersonellkategori] = receivedSykmelding.legeHelsepersonellkategori
                this[utenlandskSykmelding] = receivedSykmelding.utenlandskSykmelding
                this[sykmelding] = receivedSykmelding.sykmelding
                this[validationResult] = receivedSykmelding.validationResult
            }
        }
    }
}
