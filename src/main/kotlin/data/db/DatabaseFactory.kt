package com.meesam.data.db

import com.meesam.data.tables.AttributeTable
import com.meesam.data.tables.CartProductsTable
import com.meesam.data.tables.CategoryTable
import com.meesam.data.tables.ProductAttributesTable
import com.meesam.data.tables.ProductImagesTable
import com.meesam.data.tables.ProductTable
import com.meesam.data.tables.RefreshTokensTable
import com.meesam.data.tables.UserAddressTable
import com.meesam.data.tables.UserCartTable
import com.meesam.data.tables.UserFavProductTable
import com.meesam.data.tables.UserTable
import com.meesam.data.tables.UserWishListTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

import kotlinx.coroutines.Dispatchers
import java.sql.Connection

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        val config = hikari(environment)
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        // Optional: set default isolation
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ

        // Dev-time auto schema sync (use Flyway in production)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserTable,
                UserWishListTable,
                AttributeTable,
                CartProductsTable,
                CategoryTable,
                ProductTable,
                ProductAttributesTable,
                ProductImagesTable,
                UserAddressTable,
                UserCartTable,
                UserFavProductTable,
                RefreshTokensTable)
        }
    }

    private fun hikari(environment: ApplicationEnvironment): HikariConfig {
        val cfg = HikariConfig().apply {
            jdbcUrl = environment.config.propertyOrNull("db.jdbcUrl")?.getString()
                ?: "jdbc:postgresql://localhost:5432/ktor_app_db"
            driverClassName = environment.config.property("db.driver").getString()
            username = environment.config.propertyOrNull("db.user")?.getString() ?: "postgres"
            password = environment.config.propertyOrNull("db.password")?.getString() ?: "admin"
            maximumPoolSize = environment.config.propertyOrNull("db.maximumPoolSize")?.getString()?.toInt() ?: 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return cfg
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}