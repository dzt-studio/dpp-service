package dzt.studio.dppservice.service

import dzt.studio.dppservice.domain.job.DppJobScheduled
import dzt.studio.dppservice.util.PageRequest
import dzt.studio.dppservice.util.PageResult

interface CronService {
    fun getList(pageRequest: PageRequest?): PageResult?

    fun createCronJob(dppJobScheduled: DppJobScheduled):Boolean

    fun getEnableScheduleJob():List<String>?

    fun updateCron(dppJobScheduled: DppJobScheduled):Boolean

    /**
     * 重启定时任务
     */
    fun restartTaskList(id: String?):Boolean

    fun stopTaskJob(id: String?):Boolean

    fun deleteCron(id: String?):Boolean

}