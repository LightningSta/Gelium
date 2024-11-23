package org.example.gateway.Security.Filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import org.example.gateway.Entity.Person;
import org.example.gateway.Security.DetailsService.PersonDetailService;
import org.example.gateway.Security.JWT.JWTtoken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class JwtFilter implements WebFilter {

    @Autowired
    private AuthenticationProvider  authenticationProvider;

    @Autowired
    private PersonDetailService personDetailService;

    @Autowired
    private JWTtoken jwTtoken;


    public Authentication validateToken(String token) {
        token=token.replace("Bearer ", "");
        try {
            JwtParserBuilder jwtParserBuilder = Jwts.parser();
            jwtParserBuilder.setSigningKey(jwTtoken.generateKey());
            JwtParser jwtParser = jwtParserBuilder.build();
            Claims claims =  jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();  // Извлекаем имя пользователя

            if (username != null) {
                UserDetails userDetails = personDetailService.loadUserByUsername(username);
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
        return null;
    }

    public Person getPersonFromToken(String token) {
        token=token.replace("Bearer ", "");
        try {
            JwtParserBuilder jwtParserBuilder = Jwts.parser();
            jwtParserBuilder.setSigningKey(jwTtoken.generateKey());
            JwtParser jwtParser = jwtParserBuilder.build();
            Claims claims =  jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();  // Извлекаем имя пользователя

            if (username != null) {
                return personDetailService.getPerson(username);
            }
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
        return null;
    }
    private Boolean validateTokenDB(String token) {
        token=token.replace("Bearer ", "");
        try {
            JwtParserBuilder jwtParserBuilder = Jwts.parser();
            jwtParserBuilder.setSigningKey(jwTtoken.generateKey());
            JwtParser jwtParser = jwtParserBuilder.build();
            Claims claims =  jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();  // Извлекаем имя пользователя

            if (username != null && username.equals("supreme")) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
        return false;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (exchange.getRequest().getPath().toString().equals("/register")
        || exchange.getRequest().getPath().toString().equals("/login") ||
        exchange.getRequest().getPath().toString().equals("/check")) {
            return chain.filter(exchange);
        }
        else if(exchange.getRequest().getPath().toString().contains("db")){
            if(validateTokenDB(authHeader)){
                return chain.filter(exchange);
            }else if (validateToken(authHeader)==null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            } else {
                return chain.filter(exchange);
            }
        }
        else if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return exchange.getResponse().setComplete();
            /*
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffers -> {
                        byte[] bytes = new byte[dataBuffers.readableByteCount()];
                        dataBuffers.read(bytes);
                        DataBufferUtils.release(dataBuffers);

                        String body = new String(bytes);
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .build();

                        ServerWebExchange mutatedExchange = new ServerWebExchangeDecorator(exchange) {
                            @Override
                            public ServerHttpRequest getRequest() {
                                return new ServerHttpRequestDecorator(mutatedRequest) {
                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                                    }
                                };
                            }
                        };
                        System.out.println(body);
                        if (!body.isEmpty()) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Person person = objectMapper.readValue(body, Person.class);
                                if (person != null && !person.getUsername().isEmpty() && !person.getPassword().isEmpty()) {
                                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                                            person.getUsername(),
                                            person.getPassword(),
                                            null
                                    );

                                    authentication = authenticationProvider.authenticate(authentication);
                                    if (authentication != null) {
                                        // Если аутентификация успешна, генерируем токен
                                        String token = jwTtoken.generateToken(authentication);
                                        exchange.getResponse().getHeaders().add("Authorization", "Bearer " + token);
                                        return chain.filter(mutatedExchange);
                                    } else {
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                                                .bufferFactory()
                                                .wrap("Authentication failed".getBytes())));
                                    }
                                }
                            } catch (Exception e) {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                                        .bufferFactory()
                                        .wrap("Authentication failed".getBytes())));
                            }
                        }else{
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                        return null;
                    });
             */
        } else {
            // Если токен присутствует, его нужно проверить
            String authToken = authHeader.substring(7); // Извлекаем токен
            Authentication authentication = validateToken(authToken);

            if (authentication != null) {
                // Токен валиден
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                // Токен невалиден
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        /*
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null) {
            // Если заголовок отсутствует, прерываем выполнение запроса (например, возвращаем ошибку 401 Unauthorized)
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Завершаем выполнение запроса
        } else {
            // Если заголовок присутствует, продолжаем выполнение запроса
            return chain.filter(exchange);
        }

         */
    }

}
