package com.example.sweater.domain;

import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String text;
    private String tag;

    //одному пользователю множество сообщений
    @ManyToOne(fetch = FetchType.EAGER)
    //для того что бы в БД поле называлось user_id, а не author
    @JoinColumn(name = "user_id")
    private User author;

    private String filename;

    public Message() {
    }

    public Message(String text, String tag, User author){
        this.text = text;
        this.tag = tag;
        this.author = author;
    }

    //проверяет, есть ли автор
    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getFilename() { return filename; }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
