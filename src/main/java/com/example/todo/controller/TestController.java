package com.example.todo.controller;

import com.example.todo.dto.RequestBodyDTO;
import com.example.todo.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public String testController(){
        return "hello";
    }

    @GetMapping("/{id}")
    public String testWithPathVariables(@PathVariable(required = false) int id){
        return "id "+id;                            //이 매개변수가 꼭 필요한 것은 아니라는 뜻
    }

    @GetMapping("/testRequestParam") // GET http://localhost:8000/test/testRequestParam?id=3333 : request id 3333
    public String testRequestParam(@RequestParam(required = false) int id){
        return "request id "+id;
    }

    @GetMapping("/testRequestBody")
    public String testRequestBody(@RequestBody RequestBodyDTO requestBodyDTO){ //RequestBody로 들어온 JSON을 requestBodyDTO 오브젝트로 가져온다
        return "id : "+requestBodyDTO.getId()+", message : "+requestBodyDTO.getMessage();
    } // GET http://localhost:8000/test/testRequestBody : Body - raw - JSON - {"id":3, "message":"hi"} Send -> id : 3, message : hi


    //-------------------- return String example


    @GetMapping("/testResponseBody")
    public ResponseDTO<String> testResponseBody(){ //ResponseDTO return
        List<String> list = new ArrayList<>();
        list.add("<ResponseDTO>");
        ResponseDTO<String> response=ResponseDTO.<String>builder().data(list).build();
        return response;
    }

    @GetMapping("/testResponseEntity")
    public ResponseEntity<?> testResponseEntity(){ //ResponseDTO return, status+header 변경
        List<String> list = new ArrayList<>();
        list.add("<ResponseEntity> http status 400");
        ResponseDTO<String> response=ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.badRequest().body(response);
    }                       //badRequest()와 body return
                            //<-> ok()

}
