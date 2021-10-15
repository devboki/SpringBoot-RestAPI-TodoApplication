package com.example.todo.persistence;

import com.example.todo.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, String> {
//JpaRepository interface 확장. generic type<table entity, primary key type>를 받는다

    List<TodoEntity> findByUserId(String userId);

    //@Query("select * from todo t where t.userId = ?1")
    //List<TodoEntity> findByUserId(String userId);
    //@Query 없어도 JPA가 findByUserId를 파싱해서 SELECT * FROM TodoRepository WHERE userId='{userId}' 쿼리 작성 및 실행
    //더 복잡한 쿼리는 어노테이션 사용해서 지정하기
}
