package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.DppJobLog
import org.springframework.stereotype.Repository

@Repository
interface DppJobLogDao : MyBatisBaseDao<DppJobLog, String> {
}