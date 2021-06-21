package dzt.studio.dppservice.service

import dzt.studio.dppservice.domain.job.DppAppJars
import dzt.studio.dppservice.domain.job.JobParams
import dzt.studio.dppservice.domain.job.SaveWithJarParm
import dzt.studio.dppservice.util.PageRequest
import dzt.studio.dppservice.util.PageResult
import org.springframework.web.multipart.MultipartFile

interface JobService {
    /**
     * 获取任务列表
     */
    fun getJobList(pageRequest: PageRequest?): PageResult?

    /**
     * 创建任务
     */
    fun createJob(params: JobParams):Boolean

    fun saveJarApp(params: SaveWithJarParm):Boolean

    /**
     * 获取任务详情信息 by id
     */
    fun getJobInfo(jobName:String):JobParams?

    fun jobDelete(jobId:String):Boolean

    /**
     * 校验sql
     */
    fun sqlVerify(sqls:String):String

    fun jobCommit(jobName: String):Boolean

    fun jobStop(jobId:String):Boolean

    fun jobLog(jobId:String):String?

    fun getAppJarList():List<DppAppJars>?

    fun jarUpload(file: MultipartFile):Boolean

    fun jobCommitWithJar(jobName: String):Boolean

}