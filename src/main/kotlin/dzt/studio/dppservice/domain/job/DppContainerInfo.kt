package dzt.studio.dppservice.domain.job

import java.io.Serializable
import java.util.*

/**
 * @ClassName DppContainerInfo
 * @Description
 * @Author Tandz
 * @Date 2021-06-08 15:55
 */
class DppContainerInfo: Serializable {
    var id: String? = UUID.randomUUID().toString()

    var containerId: String? = null

    var containerName: String? = null

    var containerUrl: String? = null

    var containerMsg: String? = null

    var containerVersion: String? = null

    var containerType:Int? = 1

    var jm:Int? = null

    var tm:Int? =null

    private val serialVersionUID = 1L
}