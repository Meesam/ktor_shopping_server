package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.RefreshTokensTable
import com.meesam.domain.exceptionhandler.RefreshTokenExpiredException
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.security.MessageDigest
import com.meesam.security.RefreshTokenPlain
import io.ktor.http.parameters
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


data class RefreshTokenRecord(
    val id: Long,
    val tokenHash: String,
    val userId: Long,
    val email: String,
    val jti: String,
    val expiresAt: Instant,
    val revokedAt: Instant?,
    val replacedByJti: String?
)

class RefreshTokenRepository {
    private fun hash(token: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(token.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    private fun toRecord(row: ResultRow) = RefreshTokenRecord(
        id = row[RefreshTokensTable.id],
        tokenHash = row[RefreshTokensTable.tokenHash],
        userId = row[RefreshTokensTable.userId],
        email= row[RefreshTokensTable.email],
        jti = row[RefreshTokensTable.jti],
        expiresAt = row[RefreshTokensTable.expiresAt],
        revokedAt = row[RefreshTokensTable.revokedAt],
        replacedByJti = row[RefreshTokensTable.replacedByJti]
    )

    suspend fun save(plain: RefreshTokenPlain) = dbQuery {
        /*Delete all refresh token of the user before creating a new one */
        RefreshTokensTable.apply {
            deleteWhere {
                userId eq plain.userId
            }
            insert {
                it[tokenHash] = hash(plain.token)
                it[userId] = plain.userId
                it[email] = plain.email
                it[jti] = plain.jti
                it[expiresAt] = plain.expiresAt.toKotlinInstant()
            }
        }
    }

    suspend fun findActiveByToken(
        plainToken: String,
        now: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    ): RefreshTokenRecord? =
        dbQuery {
            val hashed = hash(plainToken)
            RefreshTokensTable
                .select(
                    RefreshTokensTable.id,
                    RefreshTokensTable.tokenHash,
                    RefreshTokensTable.revokedAt,
                    RefreshTokensTable.expiresAt,
                    RefreshTokensTable.userId,
                    RefreshTokensTable.email,
                    RefreshTokensTable.jti,
                    RefreshTokensTable.replacedByJti,
                    RefreshTokensTable.createdAt

                ).where{
                    (RefreshTokensTable.tokenHash eq hashed) and
                            (RefreshTokensTable.revokedAt.isNull()) and
                            (RefreshTokensTable.expiresAt greater now)
                }
                .limit(1)
                .singleOrNull()
                ?.let(::toRecord)
                ?:throw RefreshTokenExpiredException()

        }

    suspend fun revokeByJti(
        jti: String,
        replacedBy: String? = null,
        whenTs: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    ) = dbQuery {
        RefreshTokensTable.update(where = { RefreshTokensTable.jti eq jti }) {
            it[revokedAt] = whenTs
            it[replacedByJti] = replacedBy
        }
    }

    suspend fun revokeAllForUser(
        userId: Long,
        whenTs: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    ) = dbQuery {
        RefreshTokensTable.update(where = {
            (RefreshTokensTable.userId eq userId) and RefreshTokensTable.revokedAt.isNull()
        }) {
            it[revokedAt] = whenTs
        }
    }
}




