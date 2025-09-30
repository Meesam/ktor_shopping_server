package com.meesam.domain.exceptionhandler

open class DomainException(message: String) : RuntimeException(message)
class ResourceNotFoundException(message: String) : DomainException(message)
class ValidationException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : DomainException(message)
class ConflictException(message: String) : DomainException(message)
class InvalidCredentialsException(message: String = "Invalid username or password.") : Exception(message)
class ActiveAccountException(message: String = "Check your registered email and activate your account.") : Exception(message)
class OtpExpiredException(message: String = "OTP is expired.") : Exception(message)
class InvalidOtpException(message: String = "OTP is invalid.") : Exception(message)
class RefreshTokenExpiredException(message: String = "Refresh token is expired") : Exception(message)