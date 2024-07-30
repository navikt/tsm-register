package no.nav.tsm.plugins

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import org.koin.dsl.module
import org.koin.ktor.ext.getProperty

class Environment(
    val jdbcUrl: String,
)


fun Application.createEnvironment(): Environment {
    return Environment(
        jdbcUrl = environment.config.propertyOrNull("ktor.database.jdbcUrl")?.getString() ?: throw IllegalArgumentException("Missing property: ktor.database.jdbcUrl")
    )
}

fun Application.configureEnvironment(): Environment {
    return createEnvironment()
}
