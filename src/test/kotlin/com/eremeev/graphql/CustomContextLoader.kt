package com.eremeev.graphql

import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.MergedContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer

class CustomContextLoader : SpringBootContextLoader() {

    companion object {
        private val postgres = KPostgreSQLContainer()

        init {
            postgres.start()
        }
    }

    override fun getInlinedProperties(config: MergedContextConfiguration) =
        super.getInlinedProperties(config) +
                arrayOf(
                    "spring.r2dbc.url=r2dbc:postgresql://${postgres.containerIpAddress}:${postgres.getMappedPort(5432)}/${postgres.databaseName}",
                    "spring.r2dbc.username=${postgres.username}",
                    "spring.r2dbc.password=${postgres.password}",
                )
}

class KPostgreSQLContainer : PostgreSQLContainer<KPostgreSQLContainer>()


