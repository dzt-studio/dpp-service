package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.DppJobList
import dzt.studio.dppservice.domain.job.JobParams
import org.springframework.stereotype.Repository

@Repository
interface DppJobListDao : MyBatisBaseDao<DppJobList, String> {
    fun getJobInfo(jobId:String): JobParams?
    fun selectByJobName(jobName:String):DppJobList?
}