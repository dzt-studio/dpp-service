package dzt.studio.dppservice.util

import com.github.pagehelper.PageInfo

/**
 * @ClassName PageUtils
 * @Description
 * @Author dzt
 * @Date 2021-01-07 14:21
 */
class PageUtils {

    companion object {
        /**
         * 将分页信息封装到统一的接口
         * @param pageRequest
         * @param page
         * @return
         */
        fun getPageResult(pageRequest: PageRequest?, pageInfo: PageInfo<*>): PageResult? {
            val pageResult = PageResult()
            pageResult.pageNum = pageInfo.pageNum
            pageResult.pageSize = pageInfo.pageSize
            pageResult.totalSize = pageInfo.total
            pageResult.totalPages = pageInfo.pages
            pageResult.content = pageInfo.list
            return pageResult
        }
    }

}