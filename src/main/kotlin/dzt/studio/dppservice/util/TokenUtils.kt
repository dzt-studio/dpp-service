package dzt.studio.dppservice.util

import com.alibaba.fastjson.JSON
import dzt.studio.dppservice.domain.user.DppUser
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * @ClassName TokenUtils
 * @Description
 * @Author dzt
 * @Date 2020-10-19 15:22
 */
@Component
class TokenUtils {
    @Autowired
    var request: HttpServletRequest? = null

    @Value("\${jwt.secret}")
    private val SECRET: String? = null
    @Value("\${jwt.header}")
    private val HEADER_STRING: String? = null
    @Value("\${jwt.prefix}")
    private val TOKEN_PREFIX: String? = null

    /**
     * 根据token获取userId
     */
    fun getUser() : DppUser {
        // 从Header中拿到token
        val token = request!!.getHeader(HEADER_STRING) ?: request!!.getParameter(HEADER_STRING)
        if (token.isNullOrBlank()) {
            throw Exception("token为空")
        }
        try {
            // 解析 Token
            val claims = Jwts.parser()
                    // 验签
                    .setSigningKey(SECRET)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX!!, ""))
                    .body

            // 拿用户id
            val user = JSON.parseObject(claims.subject, DppUser::class.java)
            return user
        } catch (e: Exception) {
            throw Exception("token失效")
        }
    }



    /**
     * 获取token
     */
    fun getToken(): String {
        // 从Header中拿到token
        val token = request!!.getHeader(HEADER_STRING) ?: request!!.getParameter(HEADER_STRING)
        if (token.isNullOrBlank()) {
            throw Exception("token为空")
        } else {
            return token
        }
    }
}