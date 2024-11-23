package org.example.frontend.security.details;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DataDetailsService implements UserDetailsService {

    public UserDetails load(String username){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(2),headers);
        headers.add("Content-Type", "application/json");
        ResponseEntity<String> response=restTemplate.exchange("http://localhost:8082/check",
                HttpMethod.POST,
                entity,
                String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            JSONObject obj = new JSONObject(response.getBody());
            UserDetails userDetails= new User(
                    obj.getString("username"),
                    obj.getString("password"),
                    List.of(new SimpleGrantedAuthority(obj.getString("role")))
            );
            return userDetails;
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("username is null");
        }else{
            try {
                UserDetails details = load(username);
                return details;
            }catch (Exception e){
                throw new UsernameNotFoundException(e.getMessage());
            }
        }
    }
}
