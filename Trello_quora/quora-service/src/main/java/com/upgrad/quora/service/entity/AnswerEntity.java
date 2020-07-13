package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "getAnswerById" , query = "select a from AnswerEntity a where a.uuid = :uuid"),
                @NamedQuery(name = "checkAnswerByUser" , query = "select a from AnswerEntity a INNER JOIN UserEntity u on a.user_id = u.id where a.uuid =:ansuuid and u.uuid = :uuuid"),
                @NamedQuery(name = "getAllAnswers" , query = "select a from AnswerEntity a INNER JOIN QuestionEntity q on a.question_id = q.id where q.uuid = :uuid")
        }
)
public class AnswerEntity implements Serializable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    private String uuid;

    @Column(name = "ans")
    @NotNull
    @Size(max = 255)
    private String ans;

    @Column(name = "date")
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user_id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user_id;
    }

    public void setUser(UserEntity user_id) {
        this.user_id = user_id;
    }

    public QuestionEntity getQuestion() {
        return question_id;
    }

    public void setQuestion(QuestionEntity question_id) {
        this.question_id = question_id;
    }
}