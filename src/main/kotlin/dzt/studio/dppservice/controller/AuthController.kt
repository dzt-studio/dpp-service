package dzt.studio.dppservice.controller

import dzt.studio.dppservice.domain.user.UserLoginDTO
import dzt.studio.dppservice.service.UserService
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import dzt.studio.dppservice.util.TokenUtils
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

/**
 * @ClassName AuthController
 * @Description
 * @Author dzt
 * @Date 2021-05-13 10:25
 */
@RestController
@Component
@RequestMapping("/user")
class AuthController {

    @Resource
    var userService: UserService? = null

    @Resource
    var tokenUtils: TokenUtils? = null

    /**
     * login
     */
    @PostMapping("/login")
    fun login(@Validated @RequestBody userLoginDTO: UserLoginDTO): JSONResult<Any> {
        return userService!!.login(userLoginDTO)
    }

    @GetMapping("/info")
    fun verifyToken(@RequestParam(value = "token") token: String): JSONResult<Any>{
        return JSONResult(HttpCode.OK.code, "操作成功",tokenUtils!!.getUser())

    }
}