package dzt.studio.dppservice.util

import java.util.*
import com.sun.mail.util.MailSSLSocketFactory
import dzt.studio.dppservice.domain.MailEntity
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * @Author: 王瑞
 * @CreateTime: 2022/1/10 16:28
 * @Description:
 */
class MailBuilder(mailEntity: MailEntity) {
    private var host: String? = null
    private var password: String? = null
    private var from: String? = null
    init {
        host = mailEntity.MAIL_HOST
        password = mailEntity.MAIL_PASSWORD
        from = mailEntity.MAIL_FROM
    }
    fun sendMail(to: String, title: String, content: String): Boolean {
        val prop = Properties()
        prop.setProperty("mail.host", host)
        prop.setProperty("mail.smtp.auth", "true")
        prop.setProperty("mail.transport.protocol", "smtp")
        prop["mail.smtp.ssl.enable"] = true
        // 开启SSL加密，否则会失败
        return try {
            val sf = MailSSLSocketFactory("TLSv1.2")
            sf.isTrustAllHosts = true
            prop["mail.smtp.ssl.socketFactory"] = sf
            val session = Session.getInstance(prop)
            prop["mail.smtp.ssl.enable"] = true
            val ts = session.transport
            // 连接邮件服务器：邮箱类型，帐号，授权码代替密码（更安全）
            ts.connect(
                host,
                from,
                password
            )
            // 创建邮件对象
            val message = MimeMessage(session)
            // 指明邮件的发件人
            message.setFrom(InternetAddress(from))
            message.setRecipient(Message.RecipientType.TO, InternetAddress(to))
            //message.setRecipients(Message.RecipientType.CC, cc)
            // 邮件的标题
            message.subject = title
            // 邮件的文本内容
            message.setContent(content, "text/html;charset=UTF-8")
            // 发送邮件
            ts.sendMessage(message, message.allRecipients)
            ts.close()
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
}
