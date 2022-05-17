package dzt.studio.dppservice.config.init

import dzt.studio.dppservice.dao.job.DppJobScheduledDao
import dzt.studio.dppservice.schedule.TaskContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * @Author: 王瑞
 * @CreateTime: 2022/5/17 10:25
 * @Description:
 */
@Component
class ScheduledJobsInit : ApplicationRunner {
	var mapContainer: TaskContainer = TaskContainer().getInstance()!!

	@Autowired
	var dppJobScheduledDao: DppJobScheduledDao? = null
	override fun run(args: ApplicationArguments?) {
		val dppJobScheduledList = dppJobScheduledDao!!.getOpeningJob()
		println("加载定时调度任务 ${dppJobScheduledList.size}个: ${dppJobScheduledList.map { it.jobName }.joinToString(",")}")
		dppJobScheduledList.forEach {
			mapContainer.putMap(it)
		}
	}
}
