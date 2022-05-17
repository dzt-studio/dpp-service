package dzt.studio.dppservice.controller

import dzt.studio.dppservice.domain.job.FilterParams
import dzt.studio.dppservice.domain.job.JobParams
import dzt.studio.dppservice.domain.job.SaveWithJarParm
import dzt.studio.dppservice.service.JobService
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import dzt.studio.dppservice.util.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.annotation.Resource

/**
 * @ClassName JobController
 * @Description
 * @Author dzt
 * @Date 2021-05-13 15:11
 */
@RestController
@Component
@RequestMapping("/job")
class JobController {
    @Resource
    var jobService: JobService? = null

    @PostMapping("/list")
    fun jobList(@RequestBody pageRequest: PageRequest): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService!!.getJobList(pageRequest))
    }

    @PostMapping("/create-and-save")
    fun jobCreate(@RequestBody params: JobParams): JSONResult<Any> {
        return if (jobService!!.createJob(params)) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @PostMapping("/filter")
    fun handleFilter(@RequestBody params: FilterParams):JSONResult<Any>{
        return JSONResult(HttpCode.OK.code, "操作成功", jobService!!.handleFilter(params))
    }

    @PostMapping("/create-and-save-with-jar")
    fun jarJobCreate(@RequestBody params: SaveWithJarParm): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService!!.saveJarApp(params))
    }

    @GetMapping("/info")
    fun getJobInfo(@RequestParam(value = "jobName") jobName: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService!!.getJobInfo(jobName))
    }

    @GetMapping("/delete")
    fun jobDel(@RequestParam(value = "jobId") jobId: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService!!.jobDelete(jobId))
    }

    @PostMapping("/verify-sql")
    fun sqlVerify(@RequestBody params: JobParams): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", params.flinkSql?.let { jobService!!.sqlVerify(it) })
    }

    @GetMapping("/commit")
    fun jobCommit(@RequestParam(value = "jobName") jobName: String): JSONResult<Any> {
        return if (jobService?.jobCommit(jobName) == true) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }

    @GetMapping("/job-commit-with-jar")
    fun jobCommitWithJar(@RequestParam(value = "jobName") jobName: String): JSONResult<Any> {
        return if (jobService?.jobCommitWithJar(jobName) == true) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }
    }


    @GetMapping("/stop")
    fun jobStop(@RequestParam(value = "jobId") jobId: String): JSONResult<Any> {
        return if (jobService?.jobStop(jobId, null) == true) {
            JSONResult(HttpCode.OK.code, "操作成功")
        } else {
            JSONResult(HttpCode.OK.code, "操作失败")
        }

    }

    @GetMapping("/get-log")
    fun jobLog(@RequestParam(value = "jobId") jobId: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService?.jobLog(jobId))
    }

    @PostMapping("/jar-upload")
    fun jarUpload(@RequestParam(value = "file") file: MultipartFile, @RequestParam(value = "ctype") ctype: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService?.jarUpload(file, ctype))
    }

    @GetMapping("/app-jar-list")
    fun appJarList(@RequestParam(value = "ctype") ctype: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService?.getAppJarList(ctype))
    }

    @GetMapping("/app-container-info")
    fun getAppContainerInfo(@RequestParam(value = "appId") appId: String): JSONResult<Any> {
        return JSONResult(HttpCode.OK.code, "操作成功", jobService?.getAppContainerInfo(appId))
    }

}
