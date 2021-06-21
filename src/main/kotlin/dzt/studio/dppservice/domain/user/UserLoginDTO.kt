package dzt.studio.dppservice.domain.user

import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

/**
 * @ClassName UserLoginDTO
 * @Description
 * @Author dzt
 * @Date 2021-05-13 10:28
 */
@Validated
class UserLoginDTO(
    @get:NotBlank(message = "登录账户不能为空")
    var account: String,
    var pwd: String? = null
)
