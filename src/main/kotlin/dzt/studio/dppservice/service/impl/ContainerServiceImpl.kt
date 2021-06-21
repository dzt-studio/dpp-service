package dzt.studio.dppservice.service.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import dzt.studio.dppservice.dao.job.DppContainerInfoDao
import dzt.studio.dppservice.domain.job.DppContainerInfo
import dzt.studio.dppservice.service.ContainerService
import dzt.studio.dppservice.util.PageRequest
import dzt.studio.dppservice.util.PageResult
import dzt.studio.dppservice.util.PageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @ClassName ContainerServiceImpl
 * @Description
 * @Author dzt
 * @Date 2021-06-11 16:00
 */
@Service
class ContainerServiceImpl : ContainerService {

    @Autowired
    var dppContainerInfoDao: DppContainerInfoDao? = null

    override fun createContainer(dppContainerInfo: DppContainerInfo): Boolean {
        return try {
            dppContainerInfoDao?.insert(dppContainerInfo)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteContainer(id: String): Boolean {
        return try {
            dppContainerInfoDao?.deleteByPrimaryKey(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getList(pageRequest: PageRequest?): PageResult? {
        return pageRequest?.let { getPageInfo(it).let { PageUtils.getPageResult(pageRequest, it) } }
    }

    override fun updateContainer(dppContainerInfo: DppContainerInfo): Boolean {
        return try {
            dppContainerInfoDao?.updateByPrimaryKey(dppContainerInfo)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getContainerList(): List<DppContainerInfo> {
        return dppContainerInfoDao!!.getList()
    }

    private fun getPageInfo(pageRequest: PageRequest): PageInfo<DppContainerInfo> {
        val pageNum = pageRequest.pageNum
        val pageSize = pageRequest.pageSize
        PageHelper.startPage<Any>(pageNum, pageSize)
        val jobList = dppContainerInfoDao!!.selectAll()
        return PageInfo<DppContainerInfo>(jobList)
    }

}