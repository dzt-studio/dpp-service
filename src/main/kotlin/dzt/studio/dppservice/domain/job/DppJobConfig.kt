package dzt.studio.dppservice.domain.job

import java.io.Serializable

/**
 * @ClassName DppJobConfig
 * @Description
 * @Author dzt
 * @Date 2021-05-13 17:03
 */
class DppJobConfig: Serializable {
    var id: String? = null

    /**
     * 并行度
     */
    var parallelism: Int? = null

    /**
     * 检查点开关
     */
    var checkpointEnable: Boolean? = null

    /**
     * 检查点间隔时间
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
     * flinksql详情
     */
    var sqlDetails: String? = null

    /**
     * 任务id
     */
    var jobId: String? = null
}