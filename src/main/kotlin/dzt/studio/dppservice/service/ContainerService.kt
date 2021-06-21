package dzt.studio.dppservice.service

import dzt.studio.dppservice.domain.job.DppContainerInfo
import dzt.studio.dppservice.util.PageRequest
import dzt.studio.dppservice.util.PageResult

interface ContainerService {
    fun createContainer(dppContainerInfo: DppContainerInfo):Boolean

    fun deleteContainer(id:String):Boolean

    fun getList(pageRequest: PageRequest?): PageResult?

    fun updateContainer(dppContainerInfo: DppContainerInfo):Boolean

    fun getContainerList():List<DppContainerInfo>
}