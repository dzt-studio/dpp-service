package dzt.studio.dppservice.config.handler

import com.fasterxml.jackson.databind.ObjectMapper
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @ClassName AuthenticationAccessDeniedHandler
 * @Description
 * @Author dzt
 * @Date 2021-05-12 10:48
 */
@Component
class AuthenticationAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json;charset=UTF-8"
        val out = response.writer
        val res = JSONResult(HttpCode.UNAUTHORIZED.code, "权限不足", "")
        out.write(ObjectMapper().writeValueAsString(res))
        out.flush()
        out.close()
    }

}