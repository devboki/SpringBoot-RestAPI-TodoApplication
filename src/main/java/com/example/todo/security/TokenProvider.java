package com.example.todo.security;

import com.example.todo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Signature;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "NMA8JPctFuna59f5";

    //JWT Token CREATE
    public String create(UserEntity userEntity){
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS)); //Token 기한 1일 설정

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) //header 내용과 SECRET_KEY
                .setSubject(userEntity.getId())                 //sub
                .setIssuer("todo app")                          //iss
                .setIssuedAt(new Date())                        //iat
                .setExpiration(expiryDate)                      //exp
                .compact();
    }

    //Token 디코딩, 파싱, 토큰 위조 여부 확인
    public String validateAndGetUserId(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)  //2) .setSigningKey 로 넘어온 SECRET_KEY 를 이용해 헤더.페이로드에 서명 후 token 서명과 비교
                .parseClaimsJws(token)      //1) Base64 로 디코딩 및 파싱
                .getBody();                 //4) userId 가 필요하므로 getBody

        return claims.getSubject();         //3) 위조되지 않았다면 Subject(=userId) return
    }


}//end
