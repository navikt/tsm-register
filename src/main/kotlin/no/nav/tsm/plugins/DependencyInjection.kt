package no.nav.tsm.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(
            environmentModule(),
        )
    }
}

fun Application.environmentModule() = module {
    single { configureEnvironment() }
}
