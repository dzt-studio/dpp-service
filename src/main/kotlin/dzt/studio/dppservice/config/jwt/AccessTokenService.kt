package dzt.studio.dppservice.config.jwt

import com.alibaba.fastjson.JSON
import dzt.studio.dppservice.domain.user.DppUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

/**
 * @ClassName AccessTokenService
 * @Description
 * @Author dzt
 * @Date 2021-05-12 11:07
 */
@Service("accessTokenService")
class AccessTokenService {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 默认1天有效期（毫秒）
     */
    @Value("\${jwt.expiration}")
    private val EXPIRATION_TIME: Long = 0

    @Value("\${jwt.secret}")
    private val SECRET: String? = null

    /**
     * JWT生成方法
     */
    fun createToken(dppUser: DppUser): String {
        val userMap = HashMap<String, String>()
        userMap["id"] = dppUser.id!!
        userMap["userName"] = dppUser.userName!!
        try {
            // 生成JWT
            val JWT = Jwts.builder()
                .setSubject(JSON.toJSONString(userMap))
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact()
            return JWT

        } catch (e: Exception) {
            log.error("token生成失败 - {}", e.message)
            throw Exception("token生成失败", e)
        }
    }
}