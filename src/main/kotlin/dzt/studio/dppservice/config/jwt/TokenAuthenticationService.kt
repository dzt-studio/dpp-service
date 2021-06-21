package dzt.studio.dppservice.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import dzt.studio.dppservice.util.JSONResult
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
object TokenAuthenticationService {


    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jwt.expiration}")
    fun setExpiration(expiration: Long?) {
        EXPIRATION_TIME = expiration
    }

    @Value("\${jwt.secret}")
    fun setSecret(secret: String?) {
        SECRET = secret
    }

    @Value("\${jwt.header}")
    fun setHeader(header: String?) {
        HEADER_STRING = header
    }

    @Value("\${jwt.prefix}")
    fun setPrefix(prefix: String?) {
        TOKEN_PREFIX = prefix
    }

    var EXPIRATION_TIME: Long? = null // token有效期 1天
    private var SECRET: String? = null // JWT密码
    private var HEADER_STRING: String? = null // token前缀
    private var TOKEN_PREFIX: String? = null // token前缀

    /**
     * JWT生成方法
     */
    fun addAuthentication(response: HttpServletResponse, userId: String, authStr: String) {
        // 生成JWT
        val JWT = Jwts.builder()
            // 保存权限（角色）
            .claim("authorities", authStr)
            // 用户ID写入标题
            .setSubject(userId)
            // 有效期设置
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME!!))
            // 签名设置
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact()

        // 将 JWT 写入 body
        try {
            response.status = HttpServletResponse.SC_OK
            response.contentType = "application/json;charset=UTF-8"
            val out = response.writer
            val res = JSONResult(0, "登录成功", JWT);
            out.write(ObjectMapper().writeValueAsString(res))
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * JWT验证方法
     */
    fun getAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        // 从Header中拿到token
        val token = request.getHeader(HEADER_STRING) ?: request.getParameter(HEADER_STRING)

        if (!token.isNullOrBlank()) {
            try {
                // 解析 Token
                val claims = Jwts.parser()
                    // 验签
                    .setSigningKey(SECRET)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX!!, "")).body

                // 拿用户id
                val user = claims.subject
                // 返回验证令牌
                return if (user != null) {
                    UsernamePasswordAuthenticationToken(user, null)
                } else {
                    null
                }
            } catch (e: Exception) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json;charset=UTF-8"
                val out = response.writer

                val res = JSONResult(5, "token失效", e.message)
                out.write(ObjectMapper().writeValueAsString(res))
                out.flush()
                out.close()
            }
        }
        return null
    }
}