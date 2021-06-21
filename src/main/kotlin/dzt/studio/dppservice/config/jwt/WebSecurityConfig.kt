package dzt.studio.dppservice.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import dzt.studio.dppservice.config.filter.UrlAccessDecisionManager
import dzt.studio.dppservice.config.handler.AuthenticationAccessDeniedHandler
import dzt.studio.dppservice.util.HttpCode
import dzt.studio.dppservice.util.JSONResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletResponse

/**
 * @ClassName WebSecurityConfig
 * @Description
 * @Author dzt
 * @Date 2021-05-12 14:10
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    var urlAccessDecisionManager: UrlAccessDecisionManager? = null
    @Autowired
    var deniedHandler: AuthenticationAccessDeniedHandler? = null

    // 设置 HTTP 验证规则
    override fun configure(http: HttpSecurity) {
        // 关闭csrf验证
        http.csrf().disable()
            // 对请求进行认证
            .authorizeRequests()
            // 所有 /login 的POST请求 都放行
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            // 所有请求需要身份认证
            .anyRequest().authenticated()
            // 动态配置URL权限
            .withObjectPostProcessor(object : ObjectPostProcessor<FilterSecurityInterceptor> {
                override fun <O : FilterSecurityInterceptor> postProcess(o: O): O {
                    o.accessDecisionManager = urlAccessDecisionManager
                    return o
                }
            })
            .and()
            // 添加一个过滤器 所有访问 /login 的请求交给 JWTLoginFilter 来处理 这个类处理所有的JWT相关内容
            .addFilterBefore(JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter::class.java)
            // 添加一个过滤器验证其他请求的Token是否合法
            .addFilterBefore(JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler { _, response, _ ->
                response.status = HttpServletResponse.SC_OK
                response.contentType = "application/json;charset=UTF-8"
                val out = response.writer
                val res = JSONResult(HttpCode.OK.code, "注销成功", "")
                out.write(ObjectMapper().writeValueAsString(res))
                out.flush()
                out.close()
            }
            .and()
            // 权限不足处理器
            .exceptionHandling().accessDeniedHandler(deniedHandler)
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/user/*"
        )
    }

}