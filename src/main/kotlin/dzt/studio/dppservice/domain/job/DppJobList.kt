package dzt.studio.dppservice.domain.job

import java.io.Serializable
import java.util.*

/**
 * @ClassName DppJobList
 * @Description
 * @Author dzt
 * @Date 2021-05-13 15:32
 */
class DppJobList : Serializable {
    var id: String? = UUID.randomUUID().toString()

    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 任务状态
     */
    var jobStatus: String? = null

    /**
     * 任务开始时间
     */
    var startTime: Date? = null

    /**
     * 任务运行时长
     */
    var runTime: Long? = null

    /**
     * 容器id
     */
    var containerId: String? = null

    /**
     * 最近一次检查点
     */
    var lastCheckPointAddress: String? = null

    /**
     * 描述
     */
    var describes: String? = null

    /**
     * 是否开启调度
     */
    var enableSchedule: Boolean? = null

    /**
     * 任务类型
     */
    var jobType: String? = null

    /**
     * jar包名字
     */
    var jarName: String? = null

    /**
     * 运行参数
     */
    var appParams: String? = null

    /**
     * 主类
     */
    var mainClass: String? = null

    /**
     * flink版本号
     */
    var fv: String? = null

    var createdBy: String? = null

    var createdAt: Date? = null

    var updatedBy: String? = null

    var updatedAt: Date? = null
    var appId: String? = null

    var containerType:Int? =null
    private val serialVersionUID = 1L
}