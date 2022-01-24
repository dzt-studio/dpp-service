package dzt.studio.dppservice.dao.job

import dzt.studio.dppservice.dao.MyBatisBaseDao
import dzt.studio.dppservice.domain.job.DppAppJars
import org.springframework.stereotype.Repository

@Repository
interface DppAppJarsDao : MyBatisBaseDao<DppAppJars, String> {
    fun getInfo(jarName:String):DppAppJars?
    fun selectAll(ctype: String):List<DppAppJars>
}
