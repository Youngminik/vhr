package com.example.vhr.config;

import com.example.vhr.model.Hr;
import com.example.vhr.model.RespBean;
import com.example.vhr.service.HrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration

public class SercurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    HrService hrService;
    @Autowired
    CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
    @Autowired
    CustomUrlDecisionManager customUrlDecisionManager;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customUrlDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                })
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        Hr hr = (Hr) authentication.getPrincipal();
                        hr.setPassword(null);
                        //更新用户状态为在线
                        hrService.setUserStateToOn(hr.getId());
                        RespBean ok = RespBean.ok("登录成功！", hr);
                        String s = new ObjectMapper().writeValueAsString(ok);
                        out.write(s);
                        out.flush();
                        out.close();

                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        RespBean error = RespBean.error("登录失败！");
                        if (exception instanceof LockedException) {
                            error.setMsg("账户被锁定，请联系管理员");
                        } else if (exception instanceof CredentialsExpiredException) {
                            error.setMsg("密码已过期，请联系管理员");
                        } else if (exception instanceof AccountExpiredException) {
                            error.setMsg("账户已过期，请联系管理员");
                        } else if (exception instanceof DisabledException) {
                            error.setMsg("账户被禁用，请联系管理员");
                        } else if (exception instanceof BadCredentialsException) {
                            error.setMsg("用户名或密码错误，请重新输入或联系管理员");
                        }
                        out.write(new ObjectMapper().writer().writeValueAsString(error));
                        out.flush();
                        out.close();

                    }
                })
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        //更新用户状态为离线
                        Hr hr = (Hr) authentication.getPrincipal();
                        hrService.setUserStateToLeave(hr.getId());
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        out.write(new ObjectMapper().writeValueAsString(RespBean.ok("注销成功！")));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()

                .csrf().disable().exceptionHandling()
                //没有登录时直接访问接口，因前端直接重定向，没有经过csrf跨域处理，在页面上很难体现错误
                //这里直接在没有权限去访问接口时直接告诉用户发生了什么错误即可
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
                            throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        /*解决重启后端，前段不重启，没有重新登录，页面上的功能失效。因为没有重新登录，操作时是无登录状态*/
                        /*还有太长时间停在操作页面而没有操作就会失效问题*/
                        /*以上两种解决办法是后端响应出401（没有认证状态）状态码，跳出登录页面
                        * 前端在api.js中进行处理*/
                        response.setStatus(401);
                        PrintWriter out = response.getWriter();
                        RespBean error = RespBean.error("访问失败！");
                        if (authException instanceof InsufficientAuthenticationException) {
                            error.setMsg("请求失败，请联系管理员！");
                        }
                        out.write(new ObjectMapper().writer().writeValueAsString(error));
                        out.flush();
                        out.close();
                    }
                });
    }

}
