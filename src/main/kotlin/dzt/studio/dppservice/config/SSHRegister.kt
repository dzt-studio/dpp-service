package dzt.studio.dppservice.config

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.Session
import ch.ethz.ssh2.StreamGobbler
import dzt.studio.dppservice.domain.OSEntity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * @ClassName SSHRegisterEntity
 * @Description
 * @Author Tandz
 * @Date 2020-09-04 10:43
 */
class SSHRegister(osEntity: OSEntity) {
    /* 连接器 */
    private var connect: Connection? = null

    /* 主机（IP） */
    private var host: String? = null

    /* 连接端口 */
    private var port = 0

    /* 编码 */
    private var charset: Charset? = null

    /* 用户 */
    private var user: String? = null

    /* 密码 */
    private var password: String? = null

    init {
        this.host = osEntity.host
        this.port = osEntity.port
        this.charset = osEntity.charset
        this.user = osEntity.user
        this.password = osEntity.password
    }

    /**
     * 登录os主机
     */
    private fun login(): Boolean {
        connect = Connection(host, port)
        return try {
            connect!!.connect()
            connect!!.authenticateWithPassword(user, password)
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取返回信息
     */
    private fun processStdout(`in`: InputStream): StringBuilder? {
        val stdout: InputStream = StreamGobbler(`in`)
        val buffer = java.lang.StringBuilder()
        try {
            val br = BufferedReader(InputStreamReader(stdout, StandardCharsets.UTF_8))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                buffer.append(line).append("\n")
            }
        } catch (e: IOException) {
        }
        return buffer
    }

    /**
     * exec shell命令
     */
    @Throws(IOException::class)
    fun exec(shell: String?): java.lang.StringBuilder? {
        var inputStream: InputStream? = null
        var result: java.lang.StringBuilder? = java.lang.StringBuilder()
        try {
            // 认证登录信息
            if (login()) {
                // 登陆成功则打开一个会话
                val session: Session = connect!!.openSession()
                session.execCommand(shell)
                inputStream = session.stdout
                result = processStdout(inputStream)
                connect!!.close()
            }
        } finally {
            inputStream?.close()
        }
        return result
    }

}