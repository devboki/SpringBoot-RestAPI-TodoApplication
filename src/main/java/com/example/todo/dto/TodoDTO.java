package com.example.todo.dto;

import com.example.todo.model.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TodoDTO {
    private String id; //user id - security를 이용해서 인증
    private String title;
    private boolean done;

    public TodoDTO(final TodoEntity entity){
        this.id=entity.getId();
        this.title=entity.getTitle();
        this.done=entity.isDone();
    }

    public static TodoEntity toEntity(final TodoDTO dto){ //DTO를 Entity로 변환해서 저장
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .done(dto.isDone())
                .build();
    }
}
