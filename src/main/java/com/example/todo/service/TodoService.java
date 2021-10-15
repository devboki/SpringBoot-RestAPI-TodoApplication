package com.example.todo.service;

import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j //simple logging facade for java : 로그 라이브러리
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

     public String testService() {
        TodoEntity entity = TodoEntity.builder().title("my first todo item").build(); // TodoEntity 생성
        repository.save(entity); // TodoEntity 저장
        TodoEntity savedEntity = repository.findById(entity.getId()).get(); // TodoEntity 검색
        return savedEntity.getTitle();
        // GET http://localhost:8000/todo/test -> error : null, data : my first todo item
    }

    /* public String testService(){
        return "<test service>";
    }*/

    //CREATE
    public List<TodoEntity> create(final TodoEntity entity){
        if(entity == null){ //Validation : 검증
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }
        if(entity.getUserId() == null){
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
        repository.save(entity);
        log.info("Entity Id : {} is saved.", entity.getId());
        return repository.findByUserId(entity.getUserId());
    }

    //검증 부분은 계속 쓰일 것이므로 private method로 리팩토링
    private void validate(final TodoEntity entity){
        if(entity == null){
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }
        if(entity.getUserId() == null){
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }

    //RETRIEVE : 검색
    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }

    //UPDATE
    public List<TodoEntity> update(final TodoEntity entity){
        validate(entity);                                                          //1) 검증 : entity가 유효한지 확인
        final Optional<TodoEntity> original = repository.findById(entity.getId()); //2) entity.getId()로 TodoEntity 호출

        if(original.isPresent()) {                  //3) TodoEntity가 존재하면 새 entity 값으로 덮어 씌운다
            final TodoEntity todo = original.get();
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            repository.save(todo);                  //4) DB에 저장
        }
        return retrieve(entity.getUserId());        //5) 사용자의 모든 entity return
    }

    //DELETE
    public List<TodoEntity> delete(final TodoEntity entity){
        validate(entity);                          //1) 검증 : entity가 유효한지 확인
        try {
            repository.delete(entity);             //2) Entity 삭제
        } catch(Exception e){
            log.error("error deleting entity ", entity.getId(), e); //3) 예외 발생시 error message 와 id logging
            throw new RuntimeException("error deleting entity "+ entity.getId());   //4) controller 로 exception 보내기
        }
        return retrieve(entity.getUserId());       //5) 사용자의 모든 entity return
    }

}//end
