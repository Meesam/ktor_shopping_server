package com.meesam.utills

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ConstraintViolation


object BeanValidation {
    val validator: Validator by lazy {
        Validation.buildDefaultValidatorFactory().validator
    }

    fun <T> errorsFor(bean: T): List<String> =
        validator.validate(bean).map { v: ConstraintViolation<T> ->
            "${v.propertyPath}: ${v.message}"
        }
}
