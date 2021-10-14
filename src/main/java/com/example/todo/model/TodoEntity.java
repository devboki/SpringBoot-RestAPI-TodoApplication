package com.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder //오브젝트 생성하는 디자인 패턴
@NoArgsConstructor //디폴트 생성자
@AllArgsConstructor //모든 변수를 인자로 받는 생성자
@Data //getter, setter
@Entity //java class를 entity로 선언
@Table(name="todo") //class와 table의 이름이 같다면 생략
public class TodoEntity {
    @Id //primery key 선언
    @GeneratedValue(generator = "system-uuid") //system-uuid라는 generator(매개변수)로 id 자동생성
    @GenericGenerator(name="system-uuid", strategy = "uuid") //generator custom
    private String id;

    private String userId;

    private String title;

    private boolean done; //true - todo checked
}
