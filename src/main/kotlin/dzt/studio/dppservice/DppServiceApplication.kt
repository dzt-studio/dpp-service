package dzt.studio.dppservice

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@MapperScan("dzt.studio.dppservice.dao")
@EnableTransactionManagement
class DppServiceApplication

fun main(args: Array<String>) {
    runApplication<DppServiceApplication>(*args)
}
