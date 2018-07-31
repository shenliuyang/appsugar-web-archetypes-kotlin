package org.appsugar.archetypes.common.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response(val code: Int = 0, val msg: String = "success", val data: Any? = null) {
    constructor(data: Any) : this(0, "success", data)

    companion object {
        val UN_AUTHENTICATED = Response(401, "Unauthenticated")
        val UN_AUTHROIZED = Response(403, "Unauthorized")
        val SUCCESS = Response()
        val ERROR = Response(-1, "error")

        fun error(msg: String) = Response(-1, msg)
    }
}