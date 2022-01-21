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
    var containerType:Int? = 2
    var jm:Int? = null
    var tm:Int?=null
    var ys:Int? = null
    var enableWarning:Boolean = false
    var warnType:String? = null
    var dingTokenId:String? = null
    var emailAdd:String? = null
    var warnTo:String? = null
    var ctype:String? = null
}
