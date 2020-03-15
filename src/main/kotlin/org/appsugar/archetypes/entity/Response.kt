package org.appsugar.archetypes.entity

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(val code: Int = 0, val msg: String = "success", val data: T? = null) {
    constructor(data: T) : this(0, "success", data)

    companion object {
        val UN_AUTHENTICATED = Response<Void>(401, "Unauthenticated")
        val UN_AUTHORIZED = Response<Void>(403, "Unauthorized")
        val SUCCESS = Response<Void>()
        val ERROR = Response<Void>(-1, "error")

        fun error(msg: String) = Response<Void>(-1, msg)
    }
}