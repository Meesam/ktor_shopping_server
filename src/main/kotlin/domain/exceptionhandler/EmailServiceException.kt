package com.meesam.domain.exceptionhandler

class EmailServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)