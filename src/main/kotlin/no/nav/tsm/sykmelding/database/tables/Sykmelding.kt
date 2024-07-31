package no.nav.tsm.sykmelding.database.tables

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.json.Json
import no.nav.tsm.smregister.models.Merknad
import no.nav.tsm.smregister.models.Sykmelding
import no.nav.tsm.smregister.models.UtenlandskSykmelding
import no.nav.tsm.smregister.models.ValidationResult
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.jsonb

private val objectMapper: ObjectMapper =
    jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }


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
    val merknader = jsonb<List<Merknad>>("merknader", { objectMapper.writeValueAsString(it) }, { objectMapper.readValue(it) }) .nullable()
    val partnerreferansef = text("partnerreferanse").nullable()
    val legeHpr = text("lege_hpr").nullable()
    val legeHelsepersonellkategori = text("lege_helsepersonellkategori").nullable()
    val utenlandskSykmelding = jsonb<UtenlandskSykmelding>("utenlandsk_sykmelding", { objectMapper.writeValueAsString(it) }, { objectMapper.readValue(it) }).nullable()
    val sykmelding = jsonb<Sykmelding>("sykmelding", { objectMapper.writeValueAsString(it) }, { objectMapper.readValue(it) })
    val validationResult = jsonb<ValidationResult>("validation_result", { objectMapper.writeValueAsString(it) }, { objectMapper.readValue(it) })
    override val primaryKey = PrimaryKey(id)
}
