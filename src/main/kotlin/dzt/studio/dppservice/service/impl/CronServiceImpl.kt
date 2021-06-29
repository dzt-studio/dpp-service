package dzt.studio.dppservice.service.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import dzt.studio.dppservice.dao.job.DppJobListDao
import dzt.studio.dppservice.dao.job.DppJobScheduledDao
import dzt.studio.dppservice.domain.job.DppJobScheduled
import dzt.studio.dppservice.schedule.TaskContainer
import dzt.studio.dppservice.service.CronService
import dzt.studio.dppservice.util.PageRequest
import dzt.studio.dppservice.util.PageResult
import dzt.studio.dppservice.util.PageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CronServiceImpl : CronService {

    @Autowired
    var dppJobScheduledDao: DppJobScheduledDao? = null

    var mapContainer: TaskContainer = TaskContainer().getInstance()!!

    @Autowired
    var dppJobListDAO: DppJobListDao? = null

    override fun getList(pageRequest: PageRequest?): PageResult? {
        return pageRequest?.let { getPageInfo(it).let { PageUtils.getPageResult(pageRequest, it) } }

    }

    override fun createCronJob(dppJobScheduled: DppJobScheduled): Boolean {
        return try {
            val jobInfo = dppJobScheduled.jobName?.let { dppJobListDAO?.selectByJobName(it) }
            dppJobScheduled.jobType = jobInfo?.jobType
            dppJobScheduled.createdAt = Date()
            dppJobScheduledDao?.insert(dppJobScheduled)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getEnableScheduleJob(): List<String>? {
        return dppJobScheduledDao?.getEnableScheduleJob()
    }

    override fun updateCron(dppJobScheduled: DppJobScheduled): Boolean {
        return try {
            dppJobScheduledDao?.updateByPrimaryKeySelective(dppJobScheduled)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun restartTaskList(id: String?): Boolean {
        return try {
            val taskList = DppJobScheduled()
            taskList.id = id
            taskList.isOpen = true
            dppJobScheduledDao!!.updateByPrimaryKeySelective(taskList)
            val dppJobScheduled = dppJobScheduledDao!!.selectByPrimaryKey(id!!)
            mapContainer.putMap(dppJobScheduled)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun stopTaskJob(id: String?): Boolean {
        return try {
            val taskList = DppJobScheduled()
            taskList.id = id
            taskList.isOpen = false
            dppJobScheduledDao!!.updateByPrimaryKeySelective(taskList)
            mapContainer.cancelMap(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteCron(id: String?): Boolean {
        return try {
            id?.let { dppJobScheduledDao?.deleteByPrimaryKey(it) }
            true
        }catch (e:Exception){
            false
        }
    }

    private fun getPageInfo(pageRequest: PageRequest): PageInfo<DppJobScheduled> {
        val pageNum = pageRequest.pageNum
        val pageSize = pageRequest.pageSize
        PageHelper.startPage<Any>(pageNum, pageSize)
        val jobList = dppJobScheduledDao!!.selectAll()
        jobList.forEach {
            if (it.isOpen!!) {
                it.status = "启用"
            } else {
                it.status = "停用"
            }
        }
        return PageInfo<DppJobScheduled>(jobList)
    }
}