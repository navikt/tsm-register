package no.nav.tsm.sykmelding.database

import no.nav.tsm.smregister.models.ReceivedSykmelding
import no.nav.tsm.sykmelding.database.tables.Sykmelding
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
}
