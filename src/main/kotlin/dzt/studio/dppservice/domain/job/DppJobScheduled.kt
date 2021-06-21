package dzt.studio.dppservice.domain.job

import java.io.Serializable
import java.util.*

/**
 * @ClassName DppJobScheduled
 * @Description
 * @Author Tandz
 * @Date 2021-06-09 14:39
 */
class DppJobScheduled: Serializable {
    var id: String? = null

    var jobId: String? = null

    var cron: String? = null

    var createdBy: String? = null

    var createdAt: Date? = null

    var updatedBy: String? = null

    var updatedAt: Date? = null

    var isOpen: Boolean? = null

    var serialVersionUID = 1L
}