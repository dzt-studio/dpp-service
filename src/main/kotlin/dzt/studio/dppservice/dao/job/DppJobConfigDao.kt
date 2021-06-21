package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.DppJobConfig
import org.springframework.stereotype.Repository

@Repository
interface DppJobConfigDao : MyBatisBaseDao<DppJobConfig, String> {
    fun deleteByJobId(jobId: String): Int
    fun selectByJobId(jobId: String): DppJobConfig
}