package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserCardsTable
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.AddUserCardRequest
import com.meesam.domain.dto.UserCardResponse
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserCardRepository {
    suspend fun isUserExists(userId: Long?): Boolean = dbQuery {
        try {
            userId?.let {
                UserTable.selectAll().where {
                    UserTable.id eq userId and (UserTable.isActive eq true)
                }.singleOrNull()?.let { true } ?: false
            } ?: false

        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun addNewCard(addUserCardRequest: AddUserCardRequest): Unit = dbQuery {
        try {
            val isUserExistedDB = isUserExists(addUserCardRequest.userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserCardsTable.insert {
                it[cardNumber] = addUserCardRequest.cardNumber
                it[name] = addUserCardRequest.name
                it[cvv] = addUserCardRequest.cvv
                it[expiredMonth] = addUserCardRequest.expiredMonth
                it[expiredYear] = addUserCardRequest.expiredYear
                it[userId] = addUserCardRequest.userId
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun getUserCards(userId: Long): List<UserCardResponse> = dbQuery {
        try {
            val isUserExistedDB = isUserExists(userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserCardsTable.selectAll().where { UserCardsTable.userId eq userId }.map { card ->
                UserCardResponse(
                    name = card[UserCardsTable.name],
                    cvv = card[UserCardsTable.cvv],
                    cardNumber = card[UserCardsTable.cardNumber],
                    expiryYear = card[UserCardsTable.expiredYear],
                    expiryMonth = card[UserCardsTable.expiredMonth],
                    isActive = card[UserCardsTable.isActive]
                )
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun deleteUserCard(cardId: Long): Unit = dbQuery {
        try {
            UserCardsTable.selectAll().where {
                UserCardsTable.id eq cardId
            }.singleOrNull() ?: throw ResourceNotFoundException("Card not found")
            UserCardsTable.deleteWhere {
                UserCardsTable.id eq cardId
            }
        }  catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }
}