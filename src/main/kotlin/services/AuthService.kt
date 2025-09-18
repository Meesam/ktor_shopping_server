package com.meesam.services

import com.meesam.data.repositories.AuthRepository
import com.meesam.domain.dto.UserRequest
import com.meesam.domain.dto.UserResponse

class AuthService(
private val authRepository: AuthRepository = AuthRepository()
) {

    suspend fun register(userRequest: UserRequest): UserResponse {
        return authRepository.register(userRequest)
    }

}