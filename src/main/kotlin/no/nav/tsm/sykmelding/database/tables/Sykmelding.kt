package no.nav.tsm.sykmelding.database.tables

import kotlinx.serialization.json.Json
import no.nav.tsm.smregister.models.Merknad
import no.nav.tsm.smregister.models.Sykmelding
import no.nav.tsm.smregister.models.UtenlandskSykmelding
import no.nav.tsm.smregister.models.ValidationResult
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.jsonb

object Sykmelding : Table() {
    val id = text("id")
    val pasientFnr = text("pasient_fnr")
    val pasientAktoerId = text("pasient_aktoer_id")
    val legeFnr = text("lege_fnr")
    val legeAktoerId = text("lege_aktoer_id")
    val mottakId = text("mottak_id")
    val legekontorOrgNr = text("legekontor_org_nr").nullable()
    val legekontorHerId = text("legekontor_her_id").nullable()
    val legekontorReshId = text("legekontor_resh_id").nullable()
    val epjSystemNavn = text("epj_system_navn")
    val epjSystemVersjon = text("epj_system_versjon")
    val mottattTidspunkt = datetime("mottatt_tidspunkt")
    val tssId = text("tss_id").nullable()
    val merknader = jsonb<List<Merknad>>("merknader", Json).nullable()
    val partnerreferansef = text("partnerreferanse").nullable()
    val legeHpr = text("lege_hpr").nullable()
    val legeHelsepersonellkategori = text("lege_helsepersonellkategori").nullable()
    val utenlandskSykmelding = jsonb<UtenlandskSykmelding>("utenlandsk_sykmelding", Json).nullable()
    val sykmelding = jsonb<Sykmelding>("sykmelding", Json)
    val validationResult = jsonb<ValidationResult>("validation_result", Json)
}
