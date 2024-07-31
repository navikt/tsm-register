package no.nav.tsm.plugins

import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nav.tsm.sykmelding.SykmeldingConsumer
import org.koin.ktor.ext.get

fun Application.configureConsumers() {
    launch(Dispatchers.IO) { get<SykmeldingConsumer>().start() }
}
