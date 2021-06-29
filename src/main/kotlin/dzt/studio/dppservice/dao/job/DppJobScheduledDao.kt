package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.DppJobScheduled
import org.springframework.stereotype.Repository

@Repository
interface DppJobScheduledDao : MyBatisBaseDao<DppJobScheduled, String>{
    fun getEnableScheduleJob(): List<String>
}