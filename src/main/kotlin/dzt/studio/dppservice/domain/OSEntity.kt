package dzt.studio.dppservice.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @ClassName OSEntity
 * @Description
 * @Author dzt
 * @Date 2020-09-04 10:39
 */
@Component
class OSEntity {
    /* 主机（IP） */
    @Value("\${daphost.host}")
    val host: String? = null

    /* 连接端口 */
    @Value("\${daphost.port}")
    val port = 0

    /* 编码 */
    val charset: Charset = StandardCharsets.UTF_8

    /* 用户 */
    @Value("\${daphost.user}")
    val user: String? = null

    /* 密码 */
    @Value("\${daphost.password}")
    val password: String? = null
}