package dzt.studio.dppservice.schedule

import dzt.studio.dppservice.domain.job.DppJobScheduled
import dzt.studio.dppservice.domain.job.TaskList
import dzt.studio.dppservice.service.JobService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledFuture
import javax.annotation.PostConstruct

/**
 * @ClassName TaskContainer
 * @Description
 * @Author dzt
 * @Date 2020-09-28 11:21
 */
@Component
class TaskContainer {
    private object MapContainerInstance {
        val INSTANCE: TaskContainer= TaskContainer()
    }

    fun getInstance(): TaskContainer? {
        return MapContainerInstance.INSTANCE
    }

    var currentHashMap  = mutableMapOf<String, TaskList>()

    @Autowired
    private var threadPoolTaskScheduler: ThreadPoolTaskScheduler? = null

    @Autowired
    private var jobService: JobService?=null

    @PostConstruct
    fun init() {
        MapContainerInstance.INSTANCE.threadPoolTaskScheduler = threadPoolTaskScheduler
        MapContainerInstance.INSTANCE.jobService = jobService
    }

    fun getById(id: String?): TaskList? {
        return currentHashMap[id]
    }

    fun getMapContainer(): Map<String, TaskList?>? {
        return currentHashMap
    }

    fun putMap(dppJobScheduled: DppJobScheduled): TaskList? {
        val taskListDto = TaskList()
        taskListDto.cron=dppJobScheduled.cron
        taskListDto.jobId = dppJobScheduled.jobId
        val future = threadPoolTaskScheduler!!.schedule(Runnable {
            dppJobScheduled.jobId?.let { this.jobService!!.jobCommit(it) }
        }, CronTrigger(dppJobScheduled.cron!!))
        taskListDto.future = future
        currentHashMap[dppJobScheduled.id!!] = taskListDto
        return taskListDto
    }

    /**
     * 根据ID暂停任务,同时在容器中删除
     * @param id
     * @return
     */
    fun cancelMap(id: String?): TaskList? {
        var task: TaskList? = null
        if (currentHashMap.containsKey(id)) {
            task = currentHashMap[id]
            val future: ScheduledFuture<*> = task!!.future!!
            future.cancel(true)
            //删除任务
            currentHashMap.remove(id)
        }
        return task
    }

}