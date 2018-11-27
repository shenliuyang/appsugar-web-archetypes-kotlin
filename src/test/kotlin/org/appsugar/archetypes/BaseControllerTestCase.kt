package org.appsugar.archetypes

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.springframework.core.env.get
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class BaseControllerTestCase : BaseTestCase() {
    companion object {
        private var loginFlag = false
    }

    protected lateinit var retrofit: Retrofit

    override fun postConstruct() {
        super.postConstruct()
        prepareLogin()
    }

    fun <T> buildFacade(clazz: Class<T>) = retrofit.create(clazz)!!


    fun prepareLogin() {
        if (BaseControllerTestCase.loginFlag) return
        BaseControllerTestCase.loginFlag = true
        val loginCookie = LoginCookie()
        val client = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor(loginCookie)).build()
        retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://localhost:${env["local.server.port"]}")
                .addConverterFactory(JacksonConverterFactory.create()).build()
        val username = "admin"
        val password = "admin"
        val result = buildFacade(MainFacade::class.java).login(username, password).execute()!!
        Assertions.assertTrue(result.body()!!.code == 0, "username or password incorrect")
        loginCookie.value = result.headers()["Set-Cookie"]!!
    }

}

class LoginCookie(var value: String = "")
class HeaderInterceptor(private val loginCookie: LoginCookie) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder()
                .addHeader("Cookie", loginCookie.value)
                .build()
        return chain.proceed(request)
    }
}

class TypedResponse<T> {
    var code = 0
    var msg = ""
    var data: T? = null
    override fun toString(): String {
        return "TypedResponse(code=$code, msg='$msg', data=$data)"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TypedPage<T> {
    var content = emptyList<T>()
    var total = 0
    override fun toString(): String {
        return "TypedPage(content=$content, total=$total)"
    }
}

interface MainFacade {
    @FormUrlEncoded
    @POST("/login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<TypedResponse<Void>>
}


suspend fun <T> Call<T>.await(): T? {
    return suspendCancellableCoroutine {
        this.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                it.resume(response.body())
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                it.resumeWithException(t)
            }
        })
    }
}