package dzt.studio.dppservice.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import dzt.studio.dppservice.domain.user.DppUser
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.UndeclaredThrowableException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @ClassName JWTLoginFilter
 * @Description
 * @Author dzt
 * @Date 2021-05-12 11:36
 */
class JWTLoginFilter(url: String, authManager: AuthenticationManager) : AbstractAuthenticationProcessingFilter(
    AntPathRequestMatcher(url)
) {

    init {
        authenticationManager = authManager
    }

    /**
     * 拿到传入JSON，解析用户名密码
     */
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {

        // JSON反序列化成 AccountCredentials
        val creds = ObjectMapper().readValue(req.inputStream, DppUser::class.java)

        // 返回一个验证令牌
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                creds.userName,
                creds.password
            )
        )
    }

    override fun successfulAuthentication(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain,
                                          auth: Authentication
    ) {
        // 获取用户角色资源权限
        val authorities = auth.authorities
        var authStr: String? = null
        for (granted in authorities) {
            authStr += granted.authority + ","
        }
        // 生成令牌
        val kwUser = auth.principal as DppUser
        TokenAuthenticationService.addAuthentication(res, kwUser.id!!, authStr!!.trimEnd(',').replace("null", "")!!)
    }

    // 登录失败
    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse,
                                            failed: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json;charset=UTF-8"
        val out = response.writer

        val res = JSONResult<String>(HttpCode.UNAUTHORIZED.code, failed.message)

        if (failed is BadCredentialsException) {
            res.message = "账号或密码错误"
            res.content = failed.toString()
        }

        if (failed is InternalAuthenticationServiceException) {
            val cause = failed.cause
            if (cause is UndeclaredThrowableException) {
                val ex = cause.undeclaredThrowable
                if (ex is InvocationTargetException) {
                    res.message = ex.targetException.message
                    res.content = ex.targetException.toString()
                }
            }
        } else {
            res.content = failed.toString()
        }

        out.write(ObjectMapper().writeValueAsString(res))
        out.flush()
        out.close()
    }

}