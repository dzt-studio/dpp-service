package dzt.studio.dppservice.domain.job

/**
 * @ClassName SaveWithJarParm
 * @Description
 * @Author Tandz
 * @Date 2021-05-13 17:37
 */
class SaveWithJarParm {
    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 容器id
     */
    var containerId: String? = null

    var enableSchedule: Boolean = false
    var jarName: String? = null
    var appParams: String? = null
    var appMainClass: String? = null
    var fv: String? = null
}