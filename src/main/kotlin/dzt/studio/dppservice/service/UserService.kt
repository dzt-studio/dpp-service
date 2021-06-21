package dzt.studio.dppservice.service

import dzt.studio.dppservice.domain.user.UserLoginDTO
import dzt.studio.dppservice.util.JSONResult

interface UserService {
    fun login(userLoginDTO: UserLoginDTO): JSONResult<Any>
}