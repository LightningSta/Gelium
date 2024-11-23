package org.example.gateway.Security.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.gateway.Entity.Person;
import org.example.gateway.Security.DetailsService.PersonDetailService;
import org.example.gateway.Security.Filters.JwtFilter;
import org.example.gateway.Security.JWT.JWTtoken;
import org.example.gateway.Security.Responser.ResponseSend;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {


    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JWTtoken jwTtoken;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private ResponseSend responseSend;


    @Autowired
    private PersonDetailService personDetailService;
    @RequestMapping("/login")
    public ResponseEntity<String> login(@RequestBody String json) {
        System.out.println(json.toString());
        ResponseEntity<String> response = null;
        ObjectMapper mapper = new ObjectMapper();
        Person person = null;
        try {
            person = mapper.readValue(json, Person.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    person.getUsername(),
                    person.getPassword(),
                    null
            );

            authentication = authenticationProvider.authenticate(authentication);
            if (authentication != null) {
                // Если аутентификация успешна, генерируем токен
                String token = jwTtoken.generateToken(authentication);
                System.out.println(token);
                response = ResponseEntity.ok(token);
            }else{
                response=ResponseEntity.notFound().build();
            };
        }catch (Exception e){
            response=ResponseEntity.notFound().build();
        }finally {
            return response;
        }
    }
    @RequestMapping("/register")
    public ResponseEntity<String> register(@RequestBody String json) {
        ResponseEntity<String> response;
        ObjectMapper mapper = new ObjectMapper();
        Person person = null;
        try {
            person = mapper.readValue(json, Person.class);
            person.setNickname(new JSONObject(json).getString("nickname"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("person");
        if( personDetailService.saveUser(
                person
        )!= null && person!=null){
            try {

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        person.getUsername(),
                        person.getPassword(),
                        null
                );

                authentication = authenticationProvider.authenticate(authentication);
                if (authentication != null) {
                    // Если аутентификация успешна, генерируем токен
                    String token = jwTtoken.generateToken(authentication);
                    response = ResponseEntity.ok(token);
                }else{
                    response=ResponseEntity.notFound().build();
                }
            }catch (Exception e){
                response=ResponseEntity.notFound().build();
                return response;
            }
        }else{
            response=ResponseEntity.notFound().build();
        }
        System.out.println(response);
        return response;
    }
    @RequestMapping("/validate")
    public ResponseEntity<String> validate(@RequestHeader HttpHeaders headers) {
        Person person = null;
        String token = headers.get("Authorization").get(0).substring(7);
        System.out.println("validate "+token);
        person =  jwtFilter.getPersonFromToken(token);
        if (person != null) {
            return ResponseEntity.ok(person.toJson());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @RequestMapping("/check")
    public ResponseEntity<String> check(@RequestBody String json){
        ResponseEntity<String> response = null;
        Person person = responseSend.checkUser(new JSONObject(json).getString("username"));
        if(person!=null){
            response=ResponseEntity.ok(person.toJson());
        }else{
            response=ResponseEntity.notFound().build();
        }
        return response;
    }
}
