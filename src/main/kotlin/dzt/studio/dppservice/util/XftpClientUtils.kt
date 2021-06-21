package dzt.studio.dppservice.util

import com.jcraft.jsch.*
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.util.*


/**
 * @ClassName XftpClientUtils
 * @Description
 * @Author Tandz
 * @Date 2021-02-01 10:20
 */
class XftpClientUtils {
    /**
     * 连接sftp服务器
     *
     * @param host
     * 主机
     * @param port
     * 端口
     * @param username
     * 用户名
     * @param password
     * 密码
     * @return
     */
    fun connect(host: String, port: Int, username: String?,
                password: String?): ChannelSftp? {
        var sftp: ChannelSftp? = null
        try {
            val jsch = JSch()
            jsch.getSession(username, host, port)
            val sshSession: Session = jsch.getSession(username, host, port)
            println("Session created.")
            sshSession.setPassword(password)
            val sshConfig = Properties()
            sshConfig.put("StrictHostKeyChecking", "no")
            sshSession.setConfig(sshConfig)
            sshSession.connect()
            println("Session connected.")
            println("Opening Channel.")
            val channel: Channel = sshSession.openChannel("sftp")
            channel.connect()
            sftp = channel as ChannelSftp
            println("Connected to $host.")
        } catch (e: Exception) {
        }
        return sftp
    }

    /**
     * 上传文件
     *
     * @param directory
     * 上传的目录
     * @param uploadFile
     * 要上传的文件
     * @param sftp
     */
    fun upload(directory: String?, file: MultipartFile, sftp: ChannelSftp):Boolean {
        return try {
            sftp.cd(directory)
            sftp.put(file.inputStream, file.originalFilename)
            sftp.exit()
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            sftp.exit()
            false
        }
    }

    /**
     * 下载文件
     *
     * @param directory
     * 下载目录
     * @param downloadFile
     * 下载的文件
     * @param saveFile
     * 存在本地的路径
     * @param sftp
     */
    fun download(directory: String?, downloadFile: String?,
                 saveFile: String?, sftp: ChannelSftp) {
        try {
            sftp.cd(directory)
            val file = File(saveFile)
            val out = FileOutputStream(file)
            val `in`: InputStream? = sftp[downloadFile]
            val bf = BufferedReader(InputStreamReader(`in`))
            val sb = StringBuffer()
            var line: String? = null
            while (bf.readLine().also { line = it } != null) {
                sb.append("""
    $line
    
    """.trimIndent())
            }
            println(sb)
            sftp[downloadFile, out]
            out?.close()
            if (`in` != null) {
                `in`.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 删除文件
     *
     * @param directory
     * 要删除文件所在目录
     * @param deleteFile
     * 要删除的文件
     * @param sftp
     */
    fun delete(directory: String?, deleteFile: String?, sftp: ChannelSftp) {
        try {
            sftp.cd(directory)
            sftp.rm(deleteFile)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     * 要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    @Throws(SftpException::class)
    fun listFiles(directory: String?, sftp: ChannelSftp): Any? {
        return sftp.ls(directory)
    }


}