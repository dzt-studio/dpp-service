package dzt.studio.dppservice.util

/**
 * @ClassName PageResult
 * @Description
 * @Author dzt
 * @Date 2020-12-28 17:20
 */
class PageResult {
    /**
     * 当前页码
     */
    var pageNum = 0

    /**
     * 每页数量
     */
    var pageSize = 0

    /**
     * 记录总数
     */
     var totalSize: Long = 0

    /**
     * 页码总数
     */
     var totalPages = 0

    /**
     * 数据模型
     */
     var content: List<*>? = null
}