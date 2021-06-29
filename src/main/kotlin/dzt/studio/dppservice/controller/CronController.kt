package dzt.studio.dppservice.controller

import dzt.studio.dppservice.domain.job.DppContainerInfo
import dzt.studio.dppservice.domain.job.DppJobScheduled
import dzt.studio.dppservice.service.CronService
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import dzt.studio.dppservice.util.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@RestController
@Component
@RequestMapping("/cron")
class CronController {
    @Resource
    var cronService: CronService? = null

    @PostMapping("/list")
    fun jobList(@RequestBody pageRequest: PageRequest): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", cronService!!.getList(pageRequest))
    }

    @PostMapping("/create")
    fun createContainer(@RequestBody dppJobScheduled: DppJobScheduled): JSONResult<Any> {
        return if (cronService!!.createCronJob(dppJobScheduled)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/enable-schedule-job")
    fun getEnableScheduleJob(): JSONResult<Any>{
        return JSONResult(HttpCode.OK.code, "操作成功", cronService!!.getEnableScheduleJob())
    }

    @PostMapping("/update")
    fun updateContainer(@RequestBody dppJobScheduled: DppJobScheduled): JSONResult<Any> {
        return if (cronService!!.updateCron(dppJobScheduled)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/restart")
    fun restartCron(@RequestParam(value = "id") id: String): JSONResult<Any> {
        return if (cronService!!.restartTaskList(id)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/stop")
    fun stopCron(@RequestParam(value = "id") id: String): JSONResult<Any> {
        return if (cronService!!.stopTaskJob(id)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/delete")
    fun deleteCron(@RequestParam(value = "id") id: String): JSONResult<Any> {
        return if (cronService!!.deleteCron(id)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

}