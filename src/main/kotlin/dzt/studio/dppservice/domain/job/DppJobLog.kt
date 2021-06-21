package dzt.studio.dppservice.domain.job

import java.io.Serializable

/**
 * @ClassName DppJobLog
 * @Description
 * @Author dzt
 * @Date 2021-06-15 17:07
 */
class DppJobLog(var jobId: String? = null, var logInfo: String? = null) : Serializable {
    private val serialVersionUID = 1L
}