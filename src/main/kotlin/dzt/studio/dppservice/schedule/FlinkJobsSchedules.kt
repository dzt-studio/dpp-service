package dzt.studio.dppservice.schedule

import com.alibaba.fastjson.JSON
import dzt.studio.dppservice.dao.job.DppContainerInfoDao
import dzt.studio.dppservice.dao.job.DppJobConfigDao
import dzt.studio.dppservice.dao.job.DppJobListDao
import dzt.studio.dppservice.domain.MailEntity
import dzt.studio.dppservice.domain.job.DppJobList
import dzt.studio.dppservice.domain.job.JobCheckPoint
import dzt.studio.dppservice.domain.job.JobCurrentStatus
import dzt.studio.dppservice.service.JobService
import dzt.studio.dppservice.util.DingTalkRobotBuilder
import dzt.studio.dppservice.util.JobStatus
import dzt.studio.dppservice.util.MailBuilder
import org.apache.commons.lang3.time.DateFormatUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*
import java.util.concurrent.LinkedBlockingQueue


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
    var dppJobConfigDao: DppJobConfigDao? = null

    @Autowired
    var dppJobListDAO: DppJobListDao? = null

    @Autowired
    private var jobService: JobService? = null

    @Autowired
    var mailEntity: MailEntity? = null

    @Value("\${env.name}")
    private val ENV_NAME: String? = null

    final var failJobQueue: LinkedBlockingQueue<String?>

    init {
        failJobQueue = LinkedBlockingQueue(10)
    }

    @Scheduled(cron = "0/10 * * * * ?")
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
                // 由RUNNING状态变为FAILED和FINISHED 放入队列
                if (it.warningEnable && it.jobStatus == JobStatus.RUNNING.toString() &&
                    (jobCurrentStatus.state == JobStatus.FAILED.toString() || jobCurrentStatus.state == JobStatus.FINISHED.toString())
                ) {
                    failJobQueue.put(it.jobName)
                }
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

//                if (it.warningEnable) {
//                    when (jobCurrentStatus.state) {
//                        "FAILED" -> {
//                            val msg = "异常失败"
//                            buildWarn(it.jobName!!, it.warnTo!!, msg)
//                        }
//                        "RESTARTING" -> {
//                            val msg = "正在自动重启"
//                            buildWarn(it.jobName!!, it.warnTo!!, msg)
//                        }
//                    }
//                }
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

    @Scheduled(cron = "0 0/30 * * * ?")
    fun jobMonitor() {

        if (failJobQueue.isNotEmpty()) {

            /**
             * key: 邮件#邮箱 value: failJobName
             */
            val failJobNameMap = mutableMapOf<String, List<String>>()
            /**
             *  先尝试重启一次
             */
            while (failJobQueue.isNotEmpty()) {
                val poll = failJobQueue.poll()
                if (poll != null) {
                    val jobParams = dppJobListDAO!!.getWarningJobInfo(poll)
                    when (jobParams?.jobType) {
                        "Flink Sql" ->
                            jobService!!.jobCommit(poll)
                        "Flink Jar" ->
                            jobService!!.jobCommitWithJar(poll)
                    }
                    val buildKey = "${jobParams?.warnType}#${jobParams?.warnTo}"
                    if (failJobNameMap.containsKey(buildKey)) {
                        failJobNameMap[buildKey]!!.plus(poll)
                    } else {
                        failJobNameMap[buildKey] = mutableListOf<String>().plus(poll)
                    }
                }
            }

            failJobNameMap.forEach { (k, v) ->
                val arr = k.split("#")
                buildWarn(arr[0], v.joinToString(","), arr[1], "flink任务状态异常警报 :( $ENV_NAME","异常失败")
            }

            Thread.sleep(2 * 60 * 1000)

            failJobNameMap.forEach { (k, v) ->
                val arr = k.split("#")
                val restartFailJobList = mutableListOf<String>()
                v.forEach {
                    val dppJobList = dppJobListDAO!!.selectByJobName(it)
                    if (dppJobList?.jobStatus == JobStatus.FAILED.toString() || dppJobList?.jobStatus == JobStatus.FINISHED.toString()) {
                        restartFailJobList.add(it)
                    }
                }
                if (restartFailJobList.isNotEmpty()) {
                    buildWarn(
                        arr[0],
                        restartFailJobList.joinToString(","),
                        arr[1],
                        "flink任务恢复失败警报 :( $ENV_NAME",
                        "恢复失败"
                    )
                } else {
                    buildWarn(
                        arr[0],
                        restartFailJobList.joinToString(","),
                        arr[1],
                        "flink任务恢复成功消息 :) $ENV_NAME",
                        "恢复成功"
                    )
                }
            }
        }
    }

    fun buildWarn(warnType: String, jobName: String, warnTo: String, warnTitle: String, warnMsg: String) {
        val mailBuilder = mailEntity?.let { MailBuilder(it) }
        val warnTime =
            DateFormatUtils.format(
                Date(),
                "yyyy-MM-dd HH:mm:ss"
            )

        val msg = "环境：$ENV_NAME \n 任务：$jobName \n 报警时间：$warnTime \n 报警信息：$warnMsg，请及时查看。"

        when (warnType) {
            "钉钉" -> DingTalkRobotBuilder.send(warnTo, msg)
            "邮件" -> mailBuilder?.sendMail(
                warnTo,
                warnTitle,
                msg
            )
        }

    }


}
