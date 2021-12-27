package dzt.studio.dppservice.schedule

import com.alibaba.fastjson.JSON
import dzt.studio.dppservice.dao.job.DppContainerInfoDao
import dzt.studio.dppservice.dao.job.DppJobListDao
import dzt.studio.dppservice.domain.job.DppJobList
import dzt.studio.dppservice.domain.job.JobCheckPoint
import dzt.studio.dppservice.domain.job.JobCurrentStatus
import dzt.studio.dppservice.util.DingTalkRobotBuilder
import dzt.studio.dppservice.util.JobStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*


/**
 * @ClassName FlinkJobsSchedules
 * @Description
 * @Author dzt
 * @Date 2020-09-16 10:03
 */
@Component
@EnableScheduling
class FlinkJobsSchedules {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    var dppContainerInfoDAO: DppContainerInfoDao? = null

    @Autowired
    var dppJobListDAO: DppJobListDao? = null

    @Scheduled(cron = "0/15 * * * * ?")
    fun jobStatus() {
        val restTemplate = RestTemplate()
        val containerJobInfo = dppContainerInfoDAO!!.getJobIdWithUrl()
        containerJobInfo.forEach {
            try {
                val r = restTemplate.getForObject(
                    it.containerUrl!! + "/jobs/{1}", Any::class.java, it.appId
                )
                val jobCurrentStatus = JSON.parseObject(JSON.toJSONString(r), JobCurrentStatus::class.java)
                val rc = restTemplate.getForObject(
                    it.containerUrl!! + "/jobs/{1}/checkpoints", Any::class.java, it.appId
                )
                val jobCheckPoint = JSON.parseObject(JSON.toJSONString(rc), JobCheckPoint::class.java)
                if (!(it.jobStatus == "BUILDING" && jobCurrentStatus.state == "CANCELED")) {
                    val dppJobList = DppJobList()
                    dppJobList.id = it.jobId
                    dppJobList.jobName = jobCurrentStatus.name
                    dppJobList.jobStatus = jobCurrentStatus.state
                    dppJobList.runTime = jobCurrentStatus.duration
                    if (jobCheckPoint.latest!!.savepoint == null && jobCheckPoint.latest!!.completed != null) {
                        dppJobList.lastCheckPointAddress = jobCheckPoint.latest!!.completed!!.external_path
                    }
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                }
                if (it.warningEnable) {
                    when (jobCurrentStatus.state) {
                        "FAILED" -> {
                            val msg = "异常失败"
                            buildWarn(it.jobName!!, it.warnTo!!, msg)
                        }
                        "RESTARTING" -> {
                            val msg = "正在自动重启"
                            buildWarn(it.jobName!!, it.warnTo!!, msg)
                        }
                    }
                }
            } catch (e: Exception) {
                if (!e.localizedMessage.contains("404 Not Found")) {
                    logger.error(e.localizedMessage)
                    if (it.jobStatus == "FINISHED" && it.containerType == 2) {
                        it.containerId?.let { it1 -> dppContainerInfoDAO!!.deleteByContainerId(it1) }
                    }
                }
                if (it.jobStatus != "BUILDING") {
                    val dppJobList = DppJobList()
                    dppJobList.id = it.jobId
                    dppJobList.jobStatus = JobStatus.FINISHED.toString()
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                }
            }
        }
    }

    fun buildWarn(jobName: String, token: String, warnMsg: String) {
        val warnTime = Date()
        val msg = "\n 任务：$jobName \n 报警时间：$warnTime \n 报警信息：$warnMsg，请及时查看。"
        DingTalkRobotBuilder.send(token, msg)
    }


}