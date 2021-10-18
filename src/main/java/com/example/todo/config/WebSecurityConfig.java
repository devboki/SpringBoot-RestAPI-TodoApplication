package com.example.todo.config;

import com.example.todo.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //서블릿 필터(스프링 시큐리티=서블릿 필터의 집합)를 사용하기 위해
    // 1) 서플릿 필터 구현 : JwtAuthenticationFilter.java
    // 2) 서블릿 컨테이너에 1)을 사용하라고 알려주는 설정 작업 : WebSecurityConfig.java *

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http security builder : 시큐리티 설정을 위한 오브젝트. (web.xml 대신함)
        http.cors() //기본 cors 설정
                .and()
                .csrf().disable() //사용하지 않으므로 disable
                .httpBasic().disable() //token 을 사용하므로 basic 인증 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //session 기반이 아님을 선언
                .and()
                .authorizeRequests().antMatchers("/","/auth/**").permitAll() //괄호 안의 경로는 인증 안해도 됨
                .anyRequest().authenticated(); //이외의 경로는 인증
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        //필터 등록 method     2)jwt filter 실행한다     1)매 요청마다 cors filter 실행한 후에
        //반드시 Cors -> jwt 실행해야 하는 것은 아님

        /*
        <log : CorsFilter 다음 JwtAuthenticationFilter 실행되고 있음>
        2021-10-18 12:56:39.503  INFO 16059 --- [           main] o.s.s.web.DefaultSecurityFilterChain     :
        Will secure any request with [org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@4ae280da,
        org.springframework.security.web.context.SecurityContextPersistenceFilter@11e71181,
        org.springframework.security.web.header.HeaderWriterFilter@6ce26986,
        org.springframework.web.filter.CorsFilter@76d0ecd7,
        com.example.todo.security.JwtAuthenticationFilter@151335cb,
                     org.springframework.security.web.authentication.logout.LogoutFilter@62ea8931,
                     org.springframework.security.web.savedrequest.RequestCacheAwareFilter@222acad,
                     org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@4e958f08,
                     org.springframework.security.web.authentication.AnonymousAuthenticationFilter@57c69937,
                     org.springframework.security.web.session.SessionManagementFilter@5866731,
                     org.springframework.security.web.access.ExceptionTranslationFilter@4b3a01d8,
                     org.springframework.security.web.access.intercept.FilterSecurityInterceptor@48ccbb32]
        */

        //1) postman 에서 signup -> signin 요청시 로그인을 하면 응답에 JWT token return 확인할 수 있음
        //2) GET localhost:8000/todo - Authorization - Bearer Token - JWT Token paste - send - Body | error : null, data : [] 뜨면 인증 성공
        //3) 다른 Token 을 입력한 후 send - body | status 403 Forbidden error 발생
    }
}
