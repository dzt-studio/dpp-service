package dzt.studio.dppservice.domain.job

import java.util.concurrent.ScheduledFuture

/**
 * @ClassName TaskList
 * @Description
 * @Author Tandz
 * @Date 2020-09-28 11:23
 */
class TaskList {
     var cron: String? = null

     var jobId: String? = null

     var future: ScheduledFuture<*>? = null
}