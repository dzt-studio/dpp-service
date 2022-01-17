package dzt.studio.dppservice.util

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader


/**
 * @Author: 王瑞
 * @CreateTime: 2021/12/13 11:02
 * @Description:
 */
object JavaExecCmd {
    fun execCmd(cmd: String): StringBuilder {
        val result = StringBuilder()
        var process: Process? = null
        var bufrIn: BufferedReader? = null
        var bufrError: BufferedReader? = null
        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            //process = Runtime.getRuntime().exec(cmd)
            val builder = ProcessBuilder("/bin/sh", "-c",cmd)
            process = builder.start()

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor()

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = BufferedReader(InputStreamReader(process.inputStream, "UTF-8"))
            bufrError = BufferedReader(InputStreamReader(process.errorStream, "UTF-8"))

            // 读取输出
            var line: String? = null
            while (bufrError.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }
            while (bufrIn.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }

        } finally {
            closeStream(bufrIn)
            closeStream(bufrError)

            // 销毁子进程
            process?.destroy()
        }

        // 返回执行结果
        return result
    }

    private fun closeStream(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: Exception) {
                // nothing
            }
        }
    }
}
