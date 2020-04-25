package ru.morkovka.report.entity.error

data class ApiValidationError(val obj: String, val field: String?, val rejectedValue: Any?, val message: String) :
    ApiSubError() {
    constructor(obj: String, message: String) : this(obj = obj, field = null, rejectedValue = null, message = message)
}