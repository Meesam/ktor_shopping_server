package com.meesam.services

import com.meesam.data.repositories.AuthRepository
import com.meesam.domain.dto.ActivateUserByOtpRequest
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.ForgotPasswordRequest
import com.meesam.domain.dto.NewOtpRequest
import com.meesam.domain.dto.OtpResponse
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

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest){
        return authRepository.forgotPassword(forgotPasswordRequest)
    }

    suspend fun activateUserByOtp(activateUserByOtpRequest: ActivateUserByOtpRequest):Unit{
        return authRepository.activateUserByOtp(activateUserByOtpRequest)
    }

    suspend fun generateNewOtp(newOtpRequest: NewOtpRequest): OtpResponse{
        return authRepository.generateNewOtp(newOtpRequest)
    }

    suspend fun getUserDetailById(userId: Long): UserResponse?{
        return authRepository.getUserDetailById(userId)
    }

}