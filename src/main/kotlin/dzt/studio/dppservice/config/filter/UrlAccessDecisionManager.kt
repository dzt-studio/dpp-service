package dzt.studio.dppservice.config.filter

import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * @ClassName UrlAccessDecisionManager
 * @Description
 * @Author dzt
 * @Date 2021-05-12 10:40
 */
@Component
class UrlAccessDecisionManager : AccessDecisionManager {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun decide(authentication: Authentication, o: Any, cas: Collection<ConfigAttribute>) {
        return
        val ite = cas.iterator()
        while (ite.hasNext()) {
            val ca = ite.next()
            // 当前请求需要的权限
            val needRole = ca.attribute
            if ("MUST_LOGIN" == needRole) {
                if (authentication is AnonymousAuthenticationToken) {
                    throw BadCredentialsException("未登录")
                } else
                    throw AccessDeniedException("权限不足")
            }
            // 遍历判断该url所需的角色看用户是否具备
            for (ga in authentication.authorities) {
                if (ga.authority == needRole) {
                    // 匹配到有对应角色,则允许通过
                    return
                }
            }
        }
        throw AccessDeniedException("权限不足")
    }

    override fun supports(attribute: ConfigAttribute?): Boolean {
        return true
    }

    override fun supports(clazz: Class<*>?): Boolean {
        return true
    }

}