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
    var id: String? = UUID.randomUUID().toString()

    var jobName: String? = null

    var cron: String? = null

    var createdBy: String? = null

    var createdAt: Date? = null

    var updatedBy: String? = null

    var updatedAt: Date? = null

    var isOpen: Boolean? = false

    var jobType: String? = null

    var status: String?=null

    var serialVersionUID = 1L
}