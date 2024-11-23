package org.example.gateway.Security.DetailsService;

import org.example.gateway.Entity.Person;
import org.example.gateway.Security.JWT.JWTtoken;
import org.example.gateway.Security.Responser.ResponseSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PersonDetailService implements UserDetailsService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ResponseSend responseSend;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {;
        Person person = responseSend.checkUser(username);
        if( person !=null) {
            UserDetails person_details = new User(person.getUsername(),person.getPassword(),person.getAuthorities());
            return person_details;
        }else{
            throw new UsernameNotFoundException(username);
        }
    }


    public Person getPerson(String username){
        Person person = responseSend.checkUser(username);
        if(username!=null && person!=null) {
            return person;
        }else{
            throw new UsernameNotFoundException(username);
        }
    }

    public Person saveUser(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return responseSend.savePerson(person);
    }
}
