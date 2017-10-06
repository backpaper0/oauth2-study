package com.example.hello;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class HelloApplication {

    public static void main(final String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

    @GetMapping
    public String sayHello(final Principal principal) {
        return String.format("Hello, %1$s!", principal.getName());
    }
}
