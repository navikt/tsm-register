package no.nav.tsm.plugins

import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.koin.ktor.ext.inject

fun Application.configureDatabase() {
    val environment by inject<Environment>()
    Flyway.configure()
        .dataSource(environment.jdbcUrl, null, null)
        .validateMigrationNaming(true)
        .load()
        .migrate()
}
