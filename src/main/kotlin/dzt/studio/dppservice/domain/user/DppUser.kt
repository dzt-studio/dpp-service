package dzt.studio.dppservice.domain.user

import java.io.Serializable
import java.util.*

/**
 * @ClassName UserInfo
 * @Description
 * @Author dzt
 * @Date 2020-10-16 14:42
 */
class DppUser : Serializable {
    var id: String? = null

    var userName: String? = null

    var password: String? = null

    var createdAt: Date? = null

    var updatedAt: Date? = null

    companion object {
        private const val serialVersionUID = 1L
    }


}