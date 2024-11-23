package org.example.databaseservice.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String username;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "role", nullable = false, length = Integer.MAX_VALUE)
    private String role;

    @Column(name = "nickname", nullable = false, length = Integer.MAX_VALUE)
    private String nickname;


    public Person() {}

    public Person(JSONObject json) {
        this.username = json.getString("username");
        this.password = json.getString("password");
        this.role = json.getString("role");
        this.nickname = json.getString("nickname");
    }
}