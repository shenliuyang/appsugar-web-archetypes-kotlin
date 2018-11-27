package org.appsugar.archetypes

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.util.*


fun main(args: Array<String>) {
    var iccidList = "89860617060065258667".split(",")
    val template = RestTemplate()
    val msg = Message("#000000,UPDA:HBV000,112.95.227.15,8837,HB-R03CDG-18101-V1.0.13.bin")
    // hezhigang bbece92f-dd80-45b4-a555-26f9e98776dc
    // wagnyunda1022  fb0f638a-2e0e-4187-aa45-6182b70c5b91
    val request = HttpEntity(msg, createHeaders("wagnyunda1022", "fb0f638a-2e0e-4187-aa45-6182b70c5b91"))
    iccidList.forEach {
        println(it)
        val url = "https://api.10646.cn/rws/api/v1/devices/$it/smsMessages"
        val result = template.exchange(url, HttpMethod.POST, request, String::class.java)
        println("$it     ${result.body}")
        Thread.sleep(500)
    }
}

data class Message(val messageText: String)

fun createHeaders(username: String, password: String): HttpHeaders {
    return object : HttpHeaders() {
        init {
            val auth = "$username:$password"
            val encodedAuth = Base64.getEncoder().encode(
                    auth.toByteArray(Charsets.US_ASCII))
            val authHeader = "Basic " + String(encodedAuth)
            set("Authorization", authHeader)
        }
    }
}