package org.example.gateway.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class Person {
    private String username;
    private String password;
    private String nickname;
    private String role;
    private Integer id;
    public Person(JSONObject json) {
        this.username = json.getString("username");
        this.password = json.getString("password");
        this.id = json.getInt("id");
        this.role = json.getString("role");
        this.nickname = json.getString("nickname");
    }
    @JsonCreator
    public Person(@JsonProperty("username") String username,
                  @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("role", role);
        json.put("nickname", nickname);
        return json.toString();
    }

    public Person(UserDetails userDetails){
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            this.role = authority.getAuthority();
        }
    }

}
