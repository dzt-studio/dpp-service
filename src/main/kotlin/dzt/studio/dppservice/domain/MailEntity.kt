package dzt.studio.dppservice.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @Author: 王瑞
 * @CreateTime: 2022/1/21 14:09
 * @Description:
 */
@Component
class MailEntity {
    @Value("\${mail.host}")
    val MAIL_HOST: String? = null

    @Value("\${mail.password}")
    val MAIL_PASSWORD: String? = null

    @Value("\${mail.from}")
    val MAIL_FROM: String? = null
}
