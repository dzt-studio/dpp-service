package dzt.studio.dppservice.domain.job

import java.io.Serializable

/**
 * @ClassName ContainerJobInfo
 * @Description
 * @Author Tandz
 * @Date 2020-09-16 10:51
 */
class ContainerJobInfo: Serializable {
    /**
     * 容器地址
     */
    var containerUrl: String? = null
    /**
     * 任务id
     */
    var appId: String? = null

    var jobStatus: String? = null

    var jobId:String? = null
}