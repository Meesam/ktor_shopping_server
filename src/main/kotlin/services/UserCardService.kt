package com.meesam.services

import com.meesam.data.repositories.UserCardRepository
import com.meesam.domain.dto.AddUserCardRequest
import com.meesam.domain.dto.UserCardResponse

class UserCardService {
    private val userCardRepository = UserCardRepository()

    suspend fun addNewCard(addUserCardRequest: AddUserCardRequest){
       return userCardRepository.addNewCard(addUserCardRequest)
    }
    suspend fun getUserCards(userId: Long): List<UserCardResponse> {
        return userCardRepository.getUserCards(userId)
    }
    suspend fun deleteUserCard(cardId: Long){
        return userCardRepository.deleteUserCard(cardId)
    }
}