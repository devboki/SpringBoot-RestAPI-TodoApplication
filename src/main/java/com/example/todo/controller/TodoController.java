package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.TodoDTO;
import com.example.todo.model.TodoEntity;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }                       //ResponseEntity.ok(response) 같은 의미

    //CREATE
    @PostMapping                //JwtAuthenticationfilter.java 에서 AuthenticationPrincipal 을 String 형의 오브젝트로 지정했기 때문에 userId 를 받아오는 것
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
    //public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
        try {
            //String temporaryUserId = "temporary-user"; //임시유저아이디 선언
            TodoEntity entity = TodoDTO.toEntity(dto); //1) TodoEntity 로 변환
            entity.setId(null);                        //2) id null 로 초기화. create 할 때는 id가 없어야 하기 때문
            //entity.setUserId(temporaryUserId);       //3) 임시유저아이디 설정. 한 유저만 로그인 없이 사용 가능
            entity.setUserId(userId);
            List<TodoEntity> entities = service.create(entity); //4) 서비스를 이용해 TodoEntity 생성
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList()); //5) 자바 스트림을 이용해 리턴된 Entity list -> TodoDTO list 로 변환
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();     //6) 변환된 TodoDTO list 를 이용해 ResponseDTO 초기화
            return ResponseEntity.ok().body(response); //7) ResponseDTO return
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response); //8) 예외시 error message return
        }
    }

    //RETRIEVE
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(){
        String temporaryUserId = "temporary-user";                     //임시유저아이디 선언
        List<TodoEntity> entities = service.retrieve(temporaryUserId); //1) service.retrieve() method 로 TodoEntity list 불러오기
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList()); //2) 자바 스트림을 이용해 리턴된 TodoEntity list -> TodoDTO list 로 변환
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();     //3) 변환된 TodoDTO list 를 이용해 ResponseDTO 초기화
        return ResponseEntity.ok().body(response); //4) ResponseDTO return
    }

    //UPDATE
    @PutMapping
    //public ResponseEntity<?> updateTodo(@RequestBody TodoDTO dto){
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        //String temporaryUserId = "temporary-user";
        TodoEntity entity = TodoDTO.toEntity(dto);          //1) dto 를 entity 로 변환
        //entity.setUserId(temporaryUserId);                  //2) userId -> temporaryUserId 로 초기화
        entity.setUserId(userId);
        List<TodoEntity> entities = service.update(entity); //3) service.update 로 entity update
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());  //4) 자바 스트림을 이용해 리턴된 TodoEntity list -> TodoDTO list 로 변환
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();      //5) 변환된 TodoDTO list 를 이용해 ResponseDTO 초기화
        return ResponseEntity.ok().body(response);          //6) ResponseDTO return
    }

    //DELETE
    @DeleteMapping
    //public ResponseEntity<?> deleteTodo(@RequestBody TodoDTO dto){
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
        try {
            //String temporaryUserId = "temporary-user"; //임시유저아이디 선언
            TodoEntity entity = TodoDTO.toEntity(dto); //1) TodoEntity 로 변환
            //entity.setUserId(temporaryUserId);         //2) 임시유저아이디 설정. 한 유저만 로그인 없이 사용 가능
            entity.setUserId(userId);
            List<TodoEntity> entities = service.delete(entity); //3) service.delete 로 entity delete
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());  //4) 자바 스트림을 이용해 리턴된 TodoEntity list -> TodoDTO list 로 변환
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();      //5) 변환된 TodoDTO list 를 이용해 ResponseDTO 초기화
            return ResponseEntity.ok().body(response);          //6) ResponseDTO return
        } catch (Exception e){
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response); //8) 예외시 error message return
        }
    }

}//controller-end
