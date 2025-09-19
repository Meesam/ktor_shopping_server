package com.meesam.domain.exceptionhandler

open class DomainException(message: String) : RuntimeException(message)
class ResourceNotFoundException(message: String) : DomainException(message)
class ValidationException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : DomainException(message)
class ConflictException(message: String) : DomainException(message)
