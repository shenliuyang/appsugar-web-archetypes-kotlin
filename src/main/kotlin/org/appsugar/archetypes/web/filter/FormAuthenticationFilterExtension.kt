package org.appsugar.archetypes.web.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter
import org.appsugar.archetypes.extension.then
import org.appsugar.bean.domain.Response
import java.nio.charset.Charset
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * 重写认证失败方法，返回json数据
 */
class FormAuthenticationFilterExtension : FormAuthenticationFilter(){
    private val content:ByteArray by lazy{
        ObjectMapper().writeValueAsString(Response.UNAUTHENTICATION).toByteArray(Charset.forName("UTF-8"))
    }
    override fun onAccessDenied(request: ServletRequest, response: ServletResponse):Boolean{
       if(request is HttpServletRequest) {
           request.getHeader("X-Requested-With")?.equals("XMLHttpRequest")?.then { response.outputStream.write(content) }
       }else {
           saveRequestAndRedirectToLogin(request, response)
       }
        return false
    }

}