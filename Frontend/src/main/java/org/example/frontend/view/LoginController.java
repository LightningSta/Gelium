package org.example.frontend.view;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LoginController {

    @PostMapping("/post")
    public ResponseEntity<String> login(@RequestBody String json) {
        System.out.println(json);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        JSONObject jsonObject = new JSONObject(json);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(2),headers);
        headers.add("Content-Type", "application/json");
        try {
            ResponseEntity<String> response=restTemplate.exchange("http://localhost:8082/login",
                    HttpMethod.POST,
                    entity,
                    String.class);
            if(response.getStatusCode()== HttpStatusCode.valueOf(200)){
                return ResponseEntity.ok(response.getBody());
            }
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.notFound().build();
    }

}
