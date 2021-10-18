package com.example.todo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //서블릿 필터(스프링 시큐리티=서블릿 필터의 집합)를 사용하기 위해
    // 1) 서플릿 필터 구현 : JwtAuthenticationFilter.java *
    // 2) 서블릿 컨테이너에 1)을 사용하라고 알려주는 설정 작업 : WebSecurityConfig.java

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException{
        try{
            String token = parseBearerToken(request); //토큰 가져오기
            log.info("Filter is running...");
            if(token != null && !token.equalsIgnoreCase("null")){ //토큰 검사. JWT 이므로 인증서버에 요청하지 않고 검증 가능
                String userId=tokenProvider.validateAndGetUserId(token);    //userId 가져오기. 위조된 경우 예외처리
                log.info("Authenticated user ID : "+userId);

                //인증 완료. SecurityContextHolder 에 등록해야 인증된 사용자로 처리
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                        (userId,null, AuthorityUtils.NO_AUTHORITIES);
                        //인증된 사용자의 정보. 문자열이 아니어도 됨. 보통 UserDetails 라는 오브젝트를 넣음
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); //SecurityContext 생성
                securityContext.setAuthentication(authentication); //Context 에 인증정보 authentication 넣기
                SecurityContextHolder.setContext(securityContext); //SecurityContextHolder 에 context 로 등록
            }
        }catch(Exception ex){
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }
    
    private String parseBearerToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        //Http 요청의 헤더를 파싱해 Bearer Token return
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
