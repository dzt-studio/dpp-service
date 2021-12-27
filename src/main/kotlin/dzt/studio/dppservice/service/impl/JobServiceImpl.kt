package dzt.studio.dppservice.service.impl

import akka.util.Switch
import com.alibaba.fastjson.JSON
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.jcraft.jsch.ChannelSftp
import dzt.studio.dppservice.config.SSHRegister
import dzt.studio.dppservice.dao.job.*
import dzt.studio.dppservice.domain.OSEntity
import dzt.studio.dppservice.domain.job.*
import dzt.studio.dppservice.service.JobService
import dzt.studio.dppservice.util.*
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.TableException
import org.apache.flink.table.api.internal.TableEnvironmentImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.annotation.Resource


/**
 * @ClassName JobServiceImpl
 * @Description
 * @Author dzt
 * @Date 2021-05-13 15:47
 */
@Service
class JobServiceImpl : JobService {

    @Autowired
    var dppJobListDAO: DppJobListDao? = null

    @Autowired
    var dppJobConfigDao: DppJobConfigDao? = null

    @Autowired
    var dppContainerInfoDao: DppContainerInfoDao? = null

    @Autowired
    var dppJobLogDao: DppJobLogDao? = null

    @Autowired
    var dppAppJarsDao: DppAppJarsDao? = null

    @Autowired
    var osEntity: OSEntity? = null

    @Resource
    var tokenUtils: TokenUtils? = null

    final var threadpool: ThreadPoolExecutor? = null

    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        threadpool = ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, SynchronousQueue())
    }

    override fun getJobList(pageRequest: PageRequest?): PageResult? {
        return pageRequest?.let { getPageInfo(it).let { PageUtils.getPageResult(pageRequest, it) } }
    }

    override fun createJob(params: JobParams): Boolean {
        return try {
            saveJob(params)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun handleFilter(params: FilterParams): PageResult? {
        return params.let { getFilterPageInfo(it).let { PageUtils.getPageResult(params, it) } }
    }

    override fun saveJarApp(params: SaveWithJarParm): Boolean {
        var dppJobList = params.jobName?.let { dppJobListDAO!!.selectByJobName(it) }
        if (dppJobList == null) {
            dppJobList = DppJobList()
            dppJobList.jobStatus = JobStatus.CREATE.toString()
        }
        dppJobList.containerId = params.containerId
        dppJobList.enableSchedule = params.enableSchedule
        dppJobList.jarName = params.jarName
        dppJobList.appParams = params.appParams
        dppJobList.mainClass = params.appMainClass
        dppJobList.fv = params.fv
        var dppJobConfig = dppJobList.id?.let { dppJobConfigDao?.selectByJobId(it) }
        if (dppJobConfig == null) {
            dppJobConfig = DppJobConfig()
        }
        dppJobConfig.jobId = dppJobList.id
        dppJobConfig.containerType = params.containerType
        if (params.containerType == 2) {
            dppJobConfig.jm = params.jm
            dppJobConfig.tm = params.tm
            dppJobConfig.ys = params.ys
        }
        return try {
            dppJobListDAO!!.upsert(dppJobList)
            dppJobConfigDao!!.upsert(dppJobConfig)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getJobInfo(jobName: String): JobParams? {
        val jobParams = dppJobListDAO?.getJobInfo(jobName)
        if (jobParams!!.enableWarning) {
            when (jobParams.warnType) {
                "钉钉" -> {
                    jobParams.dingTokenId = jobParams.warnTo
                }
                "邮件" -> {
                    jobParams.emailAdd = jobParams.warnTo
                }
            }
        }
        return jobParams
    }

    @Transactional
    override fun jobDelete(jobId: String): Boolean {
        return try {
            dppJobListDAO?.deleteByPrimaryKey(jobId)
            dppJobConfigDao?.deleteByJobId(jobId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun sqlVerify(sqls: String): String {
        val p = prepareSTEnv()
        val planner = p.planner
        val parser = planner!!.parser
        var r = ""
        val psql = sqls.split(";")
        psql.forEach {
            try {
                val operations = parser!!.parse(it)
                if (operations.size != 1) {
                    throw TableException("Unsupported SQL query! executeSql() only accepts a single SQL statement of type CREATE TABLE, DROP TABLE, ALTER TABLE, CREATE DATABASE, DROP DATABASE, ALTER DATABASE, CREATE FUNCTION, DROP FUNCTION, ALTER FUNCTION, CREATE CATALOG, DROP CATALOG, USE CATALOG, USE [CATALOG.]DATABASE, SHOW CATALOGS, SHOW DATABASES, SHOW TABLES, SHOW FUNCTIONS, CREATE VIEW, DROP VIEW, SHOW VIEWS, INSERT, DESCRIBE.")
                }
            } catch (e: Exception) {
                if (!e.localizedMessage.contains("Object") && !e.localizedMessage.contains("not found")) {
                    r += e.localizedMessage
                }
            }
        }
        if (r == "") {
            r = "校验成功"
        }
        return r
    }

    override fun jobCommit(jobName: String): Boolean {
        return try {
            val config = dppJobListDAO?.getJobInfo(jobName)
            config?.flinkSql = replaceAllBlank(config?.flinkSql)
            if (config?.checkpointEnable == true) {
                config.checkpointInterval = config.checkpointInterval!! * 1000
            }
            config?.restartStrategyTime = config?.restartStrategyTime!! * 1000
            var paramsJson = JSON.toJSONString(JSON.toJSONString(config))
            paramsJson = paramsJson.replace("`", "\\`")
            when (config.containerType) {
                1 -> {
                    var command = ""
                    when (config.ctype) {
                        "yarn" -> {
                            command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                                CommandUtils.runCommand(
                                    config.fv!!,
                                    config.containerId!!,
                                    config.lastCheckPointAddress!!
                                ) + " $paramsJson 2>&1"
                            } else {
                                CommandUtils.runCommand(config.fv!!, config.containerId!!) + " $paramsJson 2>&1"
                            }
                        }
                        "docker" -> {
                            command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                                CommandUtils.runDockerSqlCommand(
                                    config.fv!!,
                                    config.containerId!!,
                                    config.lastCheckPointAddress!!
                                ) + " $paramsJson 2>&1"
                            } else {
                                CommandUtils.runDockerSqlCommand(
                                    config.fv!!,
                                    config.containerId!!
                                ) + " $paramsJson 2>&1"
                            }
                        }
                    }
                    logger.info(command)
                    val sshRegisterEntity = osEntity?.let { SSHRegister(it) }

                    val dppJobList = DppJobList()
                    dppJobList.id = config.jobId
                    dppJobList.jobStatus = JobStatus.BUILDING.toString()
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                    threadpool?.execute {
                        val r = sshRegisterEntity?.exec(command)
                        if (r?.contains("Job has been submitted with JobID") == true) {
                            val appId = r.split("Job has been submitted with JobID ")[1].trim()
                            val dppJobList = DppJobList()
                            dppJobList.id = config.jobId
                            dppJobList.jobName = config.jobName
                            dppJobList.appId = appId
                            dppJobList.jobStatus = JobStatus.RUNNING.toString()
                            dppJobList.startTime = Date()
                            dppJobList.containerId = config.containerId
                            dppJobList.enableSchedule = config.enableSchedule
                            dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)

                        } else {
                            val dppJobList = DppJobList()
                            dppJobList.id = config.jobId
                            dppJobList.jobStatus = JobStatus.FAILED.toString()
                            dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                        }
                        val dppJobLog = DppJobLog(
                            jobId = config.jobId,
                            logInfo = r.toString()
                        )
                        dppJobLogDao?.upsert(dppJobLog)
                    }
                }
                2 -> {
                    val command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                        CommandUtils.runPreJobOnYarn(
                            config.fv!!,
                            config.lastCheckPointAddress!!,
                            config.jm!!,
                            config.tm!!,
                            config.ys!!
                        ) + " $paramsJson 2>&1"
                    } else {
                        CommandUtils.runPreJobOnYarn(
                            config.fv!!,
                            config.jm!!,
                            config.tm!!,
                            config.ys!!
                        ) + " $paramsJson 2>&1"
                    }
                    logger.info(command)
                    val sshRegisterEntity = osEntity?.let { SSHRegister(it) }
                    val dppJobList = DppJobList()
                    dppJobList.id = config.jobId
                    dppJobList.jobStatus = JobStatus.BUILDING.toString()
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                    threadpool?.execute {
                        val r = sshRegisterEntity?.exec(command)
                        if (r?.contains("Job has been submitted with JobID") == true) {
                            val appId = r.split("Job has been submitted with JobID ")[1].trim()
                            val yid = r.split("Submitted application ")[1].split(" ")[0].split("\n")[0]
                            val webUI = r.split("Found Web Interface ")[1].split(" ")[0]
                            val dppJobList = DppJobList()
                            dppJobList.id = config.jobId
                            dppJobList.jobName = config.jobName
                            dppJobList.appId = appId
                            dppJobList.containerId = yid
                            dppJobList.jobStatus = JobStatus.RUNNING.toString()
                            dppJobList.startTime = Date()
                            dppJobList.enableSchedule = config.enableSchedule
                            dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                            val dppContainerInfo = DppContainerInfo()
                            dppContainerInfo.id = UUID.randomUUID().toString()
                            dppContainerInfo.containerId = yid
                            dppContainerInfo.containerType = 2
                            dppContainerInfo.containerUrl = "http://$webUI"
                            dppContainerInfo.containerVersion = config.fv
                            dppContainerInfo.jm = config.jm
                            dppContainerInfo.tm = config.tm
                            dppContainerInfoDao?.insert(dppContainerInfo)
                        } else {
                            val dppJobList = DppJobList()
                            dppJobList.id = config.jobId
                            dppJobList.jobStatus = JobStatus.FAILED.toString()
                            dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                        }
                        val dppJobLog = DppJobLog(
                            jobId = config.jobId,
                            logInfo = r.toString()
                        )
                        dppJobLogDao?.upsert(dppJobLog)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun jobStop(jobId: String): Boolean {
        val jobInfo = dppJobListDAO?.selectByPrimaryKey(jobId)
        val jobConfig = dppJobListDAO?.getJobInfo(jobInfo?.jobName!!)
        val containerType = jobConfig!!.containerType
        val ctype = jobConfig.ctype
        var command = ""
        when (ctype) {
            "yarn" -> {
                command = CommandUtils.cancelCommand(jobInfo?.fv!!, jobInfo.appId!!, jobInfo.containerId!!)
            }
            "docker" -> {
                command = CommandUtils.cancelCommand(jobInfo!!.appId!!, jobInfo.containerId!!)
            }
        }
        val sshRegisterEntity = osEntity?.let { SSHRegister(it) }
        val r = sshRegisterEntity?.exec(command)
        if (r!!.contains("Cancelled job")) {
            jobInfo?.jobStatus = JobStatus.CANCELED.toString()
            if (containerType == 2) {
                dppContainerInfoDao?.deleteByContainerId(jobInfo?.containerId!!)
            }
            dppJobListDAO!!.updateByPrimaryKeySelective(jobInfo!!)
        }
        return r.contains("Cancelled job")
    }

    override fun jobLog(jobId: String): String? {
        return dppJobLogDao?.selectByPrimaryKey(jobId)?.logInfo
    }

    override fun getAppJarList(): List<DppAppJars>? {
        return dppAppJarsDao?.selectAll()
    }

    override fun jarUpload(file: MultipartFile): Boolean {
        val sf = XftpClientUtils()
        val sftp: ChannelSftp = sf.connect(osEntity!!.host!!, osEntity!!.port, osEntity!!.user, osEntity!!.password)!!
        val isload = sf.upload("/data/flink/jar/jobs/", file, sftp)
        if (isload) {
            var dppAppJars = dppAppJarsDao?.getInfo(file.originalFilename)
            if (dppAppJars == null) {
                dppAppJars = DppAppJars()
                dppAppJars.id = UUID.randomUUID().toString()
                dppAppJars.jarName = file.originalFilename
                dppAppJars.createdAt = Date()
                dppAppJarsDao!!.insert(dppAppJars)
            } else {
                dppAppJars.updatedAt = Date()
                dppAppJarsDao?.updateByPrimaryKeySelective(dppAppJars)
            }
        }
        return isload
    }

    override fun jobCommitWithJar(jobName: String): Boolean {
        return try {
            val config = dppJobListDAO?.getJobInfo(jobName)
            var r = java.lang.StringBuilder()
            var command = ""
            when (config?.containerType) {
                1 -> {
                    when (config.ctype) {
                        "yarn" -> {
                            if (config.jarName != null) {
                                command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                                    CommandUtils.runWithAppCommand(
                                        config.mainClass!!,
                                        config.containerId!!,
                                        config.jarName!!,
                                        config.appParams,
                                        config.lastCheckPointAddress!!,
                                        config.fv!!
                                    )
                                } else {
                                    CommandUtils.runWithAppCommand(
                                        config.mainClass!!,
                                        config.containerId!!,
                                        config.jarName!!,
                                        config.appParams,
                                        config.fv!!
                                    )
                                }
                            }
                        }
                        "docker" -> {
                            if (config.jarName != null) {
                                command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                                    CommandUtils.runDockerJarCommand(
                                        config.mainClass!!,
                                        config.containerId!!,
                                        config.jarName!!,
                                        config.appParams,
                                        config.lastCheckPointAddress!!,
                                    )
                                } else {
                                    CommandUtils.runDockerJarCommand(
                                        config.mainClass!!,
                                        config.containerId!!,
                                        config.jarName!!,
                                        config.appParams,
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    if (config.jarName != null) {
                        command = if (!config.lastCheckPointAddress.isNullOrBlank()) {
                            CommandUtils.runPreJobWithApp(
                                config.mainClass!!,
                                config.jarName!!,
                                config.appParams,
                                config.lastCheckPointAddress!!,
                                config.fv!!,
                                config.jm!!,
                                config.tm!!,
                                config.ys!!
                            )
                        } else {
                            CommandUtils.runPreJobWithApp(
                                config.mainClass!!,
                                config.jarName!!,
                                config.appParams,
                                config.fv!!,
                                config.jm!!,
                                config.tm!!,
                                config.ys!!
                            )
                        }
                    }
                }

            }
            logger.info(command)
            val sshRegisterEntity = osEntity?.let { SSHRegister(it) }
            val dppJobList = DppJobList()
            dppJobList.id = config?.jobId
            dppJobList.jobStatus = JobStatus.BUILDING.toString()
            dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
            threadpool?.execute {
                r = sshRegisterEntity?.exec(command)!!
                if (r.contains("Job has been submitted with JobID")) {
                    val appId = r.split("Job has been submitted with JobID ")[1].trim()
                    val dppJobList = DppJobList()
                    dppJobList.jobName = config?.jobName
                    dppJobList.id = config?.jobId
                    dppJobList.appId = appId
                    dppJobList.jobStatus = JobStatus.RUNNING.toString()
                    dppJobList.startTime = Date()
                    if (config?.containerType == 1) {
                        dppJobList.containerId = config.containerId
                    }
                    if (config?.containerType == 2) {
                        val yid = r.split("Submitted application ")[1].split(" ")[0].split("\n")[0]
                        val webUI = r.split("Found Web Interface ")[1].split(" ")[0]
                        dppJobList.containerId = yid
                        val dppContainerInfo = DppContainerInfo()
                        dppContainerInfo.id = UUID.randomUUID().toString()
                        dppContainerInfo.containerId = yid
                        dppContainerInfo.containerType = 2
                        dppContainerInfo.containerUrl = "http://$webUI"
                        dppContainerInfo.containerVersion = config.fv
                        dppContainerInfo.jm = config.jm
                        dppContainerInfo.tm = config.tm
                        dppContainerInfoDao?.insert(dppContainerInfo)
                    }
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                } else {
                    val dppJobList = DppJobList()
                    dppJobList.id = config?.jobId
                    dppJobList.jobStatus = JobStatus.FAILED.toString()
                    dppJobListDAO!!.updateByPrimaryKeySelective(dppJobList)
                }
                val dppJobLog = DppJobLog(
                    jobId = config?.jobId,
                    logInfo = r.toString()
                )
                dppJobLogDao?.upsert(dppJobLog)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getAppContainerInfo(appId: String): String {
        return dppJobListDAO?.getAppContainerInfo(appId)!!
    }

    fun replaceAllBlank(str: String?): String? {
        var s = ""
        if (str != null) {
            val p: Pattern = Pattern.compile("\\r|\n")
            /*
            \n 回车(\u000a)
            \t 水平制表符(\u0009)
            \s 空格(\u0008)
            \r 换行(\u000d)
            */
            val m: Matcher = p.matcher(str)
            s = m.replaceAll(" ")
        }
        return s
    }

    /**
     * 调用分页插件完成分页
     * @param pageQuery
     * @return
     */
    private fun getPageInfo(pageRequest: PageRequest): PageInfo<DppJobList> {
        val pageNum = pageRequest.pageNum
        val pageSize = pageRequest.pageSize
        PageHelper.startPage<Any>(pageNum, pageSize)
        val jobList = dppJobListDAO!!.selectAll()
        return PageInfo<DppJobList>(jobList)
    }

    private fun getFilterPageInfo(params: FilterParams): PageInfo<DppJobList> {
        val pageNum = params.pageNum
        val pageSize = params.pageSize
        PageHelper.startPage<Any>(pageNum, pageSize)
        val jobList = dppJobListDAO!!.selectByParams(params)
        return PageInfo<DppJobList>(jobList)
    }

    /**
     * 保存任务
     */
    @Transactional
    fun saveJob(params: JobParams) {
        val user = tokenUtils!!.getUser()
        var dppJobList = params.jobName?.let { dppJobListDAO!!.selectByJobName(it) }
        if (dppJobList == null) {
            dppJobList = DppJobList()
            dppJobList.jobStatus = JobStatus.CREATE.toString()
        }
        dppJobList.jobName = params.jobName
        if (params.jobType != null) {
            dppJobList.jobType = params.jobType
        }
        dppJobList.containerId = params.containerId
        dppJobList.createdBy = user.userName
        dppJobList.describes = params.describes
        dppJobList.enableSchedule = params.enableSchedule
        dppJobList.fv = params.fv
        var dppJobConfig = dppJobList.id?.let { dppJobConfigDao?.selectByJobId(it) }
        if (dppJobConfig == null) {
            dppJobConfig = DppJobConfig()
        }
        dppJobConfig.parallelism = params.parallelism
        dppJobConfig.checkpointEnable = params.checkpointEnable
        if (params.checkpointEnable) {
            dppJobConfig.checkpointInterval = params.checkpointInterval
        }
        dppJobConfig.restartStrategyCount = params.restartStrategyCount
        dppJobConfig.restartStrategyTime = params.restartStrategyTime
        dppJobConfig.sqlDetails = params.flinkSql
        dppJobConfig.jobId = dppJobList.id
        dppJobConfig.containerType = params.containerType
        if (params.containerType == 2) {
            dppJobConfig.jm = params.jm
            dppJobConfig.tm = params.tm
            dppJobConfig.ys = params.ys
        }
        if (params.enableWarning) {
            dppJobConfig.warningEnable = params.enableWarning
            val warnTo = if (params.warnType == "钉钉") {
                params.dingTokenId
            } else {
                params.emailAdd
            }
            dppJobConfig.warningConfig = WarningConfig(
                warnType = params.warnType,
                warnTo = warnTo
            )
        }
        dppJobListDAO!!.upsert(dppJobList)
        dppJobConfigDao!!.upsert(dppJobConfig)
    }

    fun prepareSTEnv(): TableEnvironmentImpl {
        val bpSettings = EnvironmentSettings
            .newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build()
        return TableEnvironmentImpl.create(bpSettings)
    }

}