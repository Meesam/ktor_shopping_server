package com.meesam.services

import com.meesam.data.repositories.AuthRepository
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.UserRequest
import com.meesam.domain.dto.UserResponse

class AuthService(
private val authRepository: AuthRepository = AuthRepository()
) {

    suspend fun register(userRequest: UserRequest): UserResponse {
        return authRepository.register(userRequest)
    }

    suspend fun login(authenticationRequest: AuthenticationRequest):UserResponse{
        return authRepository.login(authenticationRequest)
    }

    suspend fun changePassword(passwordRequest: ChangePasswordRequest){
        return authRepository.changePassword(passwordRequest)
    }

}