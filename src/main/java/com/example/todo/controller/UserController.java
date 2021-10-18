package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.model.UserEntity;
import com.example.todo.security.TokenProvider;
import com.example.todo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    //JOIN
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try{
            UserEntity user = UserEntity.builder()      //요청을 이용해 저장할 user build
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    .password(userDTO.getPassword())
                    .build();
            UserEntity registeredUser = userService.create(user); //userService.create 를 이용해 repository 에 user 저장
            UserDTO responseUserDTO = UserDTO.builder()
                    .email(registeredUser.getEmail())
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO); //user 정보는 항상 하나이므로 리스트를 만드는 ResponseDTO 가 아닌 UserDTO return
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //LOGIN
    /* @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword());
        if(user != null){
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed.")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //------------ 여기까지는 로그인 상태 유지 X, 사용자의 로그인 여부를 확인하지 않음, 패스워드를 암호화하지 않음. */
    //----------------------- 이후 JWT 와 Security 를 사용해 인증

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        //UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword());
        UserEntity user = userService.getByCredentials(userDTO.getEmail(), userDTO.getPassword(), passwordEncoder);
        if(user != null){                                       //user 정보가 있으면
            final String token = tokenProvider.create(user);    //token 생성
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }else{
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed.")
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }//POST localhost:8080/auth/signup email, username, password 로 계정 생성 후
     //POST localhost:8080/auth/signin email, password 로 요청시 token return 확인



}//controller-end
