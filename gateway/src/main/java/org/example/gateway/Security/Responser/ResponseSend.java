package org.example.gateway.Security.Responser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gateway.Entity.Person;
import org.example.gateway.Security.JWT.JWTtoken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ResponseSend {


    private RestTemplate restTemplate;

    @Autowired
    private JWTtoken jwTtoken;


    public Person checkUser(String username){
        String token = jwTtoken.generateToken("supreme"); // Ваш JWT токен

        // Создайте HttpHeaders и добавьте токен в заголовок
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Создайте HttpEntity с заголовками
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Создайте RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Выполните запрос с передачей заголовков
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:8082/api/db/people/username/"+username,  // URL
                    HttpMethod.GET,                          // Метод запроса
                    entity,                                  // Тело запроса с заголовками
                    String.class                             // Тип ожидаемого ответа
            );
            // Получите тело ответа и выведите
            String responseBody = response.getBody();
            JSONObject json = new JSONObject(responseBody);
            Person person = new Person(json);
            if(json==null){
                return null;
            }
            return person;
        }catch (Exception e){
            return null;
        }

    }
    public Person savePerson(Person person){
        String token = jwTtoken.generateToken("supreme"); // Ваш JWT токен
        person.setRole("User");

        // Создайте HttpHeaders и добавьте токен в заголовок
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        // Создайте HttpEntity с заголовками
        HttpEntity<String> entity = new HttpEntity<>(person.toJson(),headers);

        System.out.println(entity);
        // Создайте RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Выполните запрос с передачей заголовков
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8082/api/db/people/save",  // URL
                HttpMethod.POST,                          // Метод запроса
                entity,                                  // Тело запроса с заголовками
                String.class                             // Тип ожидаемого ответа
        );

        // Получите тело ответа и выведите
        String responseBody = response.getBody();
        JSONObject json = new JSONObject(responseBody);
        Person person_resp = new Person(json);
        if(person_resp==null){
            return null;
        }
        return person_resp;
    }

}
