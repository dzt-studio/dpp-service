package dzt.studio.dppservice.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @ClassName JWTAuthenticationFilter
 * @Description
 * @Author dzt
 * @Date 2021-05-12 11:35
 */
class JWTAuthenticationFilter :  GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        // 验证Token
        val authentication = TokenAuthenticationService
            .getAuthentication(request as HttpServletRequest, response as HttpServletResponse)

        // 验证成功
        if (authentication != null) {
            filterChain.doFilter(request, response)
            SecurityContextHolder.getContext().authentication = authentication
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json;charset=UTF-8"
            val out = response.writer
            val res = JSONResult(HttpCode.UNAUTHORIZED.code, "token验证失败", "")
            out.write(ObjectMapper().writeValueAsString(res))
            out.flush()
            out.close()
        }
    }

}