package dzt.studio.dppservice.service.impl

import dzt.studio.dppservice.config.jwt.AccessTokenService
import dzt.studio.dppservice.dao.user.DppUserDAO
import dzt.studio.dppservice.domain.user.DppUser
import dzt.studio.dppservice.domain.user.UserLoginDTO
import dzt.studio.dppservice.service.UserService
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Author dzt
 * @Date 2021-05-13 10:42
 */
@Service
class UserServiceImpl : UserService {

    @Autowired
    val dppUserDAO: DppUserDAO? = null

    @Autowired
    var accessTokenService: AccessTokenService? = null

    override fun login(userLoginDTO: UserLoginDTO): JSONResult<Any> {
        val pwd = userLoginDTO.pwd
            ?: return JSONResult("密码不能为空")
        val kwUser = DppUser()
        kwUser.userName = userLoginDTO.account
        kwUser.password = pwd
        val user = dppUserDAO!!.selectByParameters(kwUser)
            ?: return JSONResult("账号不存在")
        if (pwd != user.password) {
            return JSONResult("密码错误")
        }
        // 生成Token
        return getToken(user, userLoginDTO)
    }

    /**
     * 生成Token
     */
    fun getToken(user: DppUser, userLoginDTO: UserLoginDTO): JSONResult<Any> {
        val token = accessTokenService!!.createToken(user)
        return JSONResult(HttpCode.OK.code, "登录成功", token)
    }

}