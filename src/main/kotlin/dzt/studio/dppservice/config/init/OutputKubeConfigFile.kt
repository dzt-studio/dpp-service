package dzt.studio.dppservice.config.init

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * @Author: 王瑞
 * @CreateTime: 2021/12/17 13:59
 * @Description:
 */
@Component
class OutputKubeConfigFile : ApplicationRunner {
    @Value("\${kube.config}")
    private val KUBE_CONFIG: String? = null

    private val path = System.getProperty("user.home") + "/.kube"

    override fun run(args: ApplicationArguments?) {
        if (!KUBE_CONFIG.isNullOrBlank()) {

            val file = File("$path/config")
            if (file.exists()) {
                println("kube config 文件存在")
                if (file.delete()) {
                    println("kube config 文件删除成功")
                } else {
                    println("kube config 文件删除失败")
                }
            } else {
                val dir = File(path)
                if (dir.mkdirs()) {
                    println("kube文件夹创建成功")
                } else {
                    println("kube文件夹创建失败")
                }
            }
            val writer = BufferedWriter(FileWriter(file))
            writer.write(KUBE_CONFIG)
            println("kube config 文件写入成功,路径: ${file.absolutePath}")
            writer.close()
        }
    }

}