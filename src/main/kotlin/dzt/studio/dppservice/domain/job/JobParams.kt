package dzt.studio.dppservice.domain.job

/**
 * @ClassName JobParams
 * @Description
 * @Author dzt
 * @Date 2020-09-04 14:07
 */
class JobParams {

    var jobId: String? = null

    /**
     * 任务名称
     */
    var jobName: String? = null

    var jobStatus: String? = null

    /**
     * 任务类型
     */

    var jobType: String? = null

    /**
     * flinksql
     */
    var flinkSql: String? = null

    /**
     * 并行度
     */
    var parallelism: Int? = null

    /**
     * 检查点开关
     */
    var checkpointEnable: Boolean = false

    /**
     * 检查点时间
     */
    var checkpointInterval: Int? = null

    /**
     * 任务异常重启次数
     */
    var restartStrategyCount: Short? = null

    /**
     * 任务异常重启间隔时长
     */
    var restartStrategyTime: Int? = null

    /**
     * 容器id
     */
    var containerId: String? = null

    var lastCheckPointAddress: String? = null

    var describes: String? = null

    var enableSchedule: Boolean = false

    var containerMsg: String? = null

    var jarName: String? = null
    var appParams: String? = null
    var mainClass: String? = null

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