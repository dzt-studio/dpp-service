package dzt.studio.dppservice.domain.job

import java.io.Serializable
import java.util.*

/**
 * @ClassName DppAppJars
 * @Description
 * @Author dzt
 * @Date 2021-06-16 15:42
 */
class DppAppJars(
    var id: String? = null,

    var jarName: String? = null,

    var ctype: String? = null,

    var createdAt: Date? = null,

    var updatedAt: Date? = null,

    ) : Serializable {
}
