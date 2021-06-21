package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.ContainerJobInfo
import dzt.studio.dppservice.domain.job.DppContainerInfo
import org.springframework.stereotype.Repository

@Repository
interface DppContainerInfoDao : MyBatisBaseDao<DppContainerInfo, String>{
    fun getJobIdWithUrl():List<ContainerJobInfo>

    fun getList():List<DppContainerInfo>
}