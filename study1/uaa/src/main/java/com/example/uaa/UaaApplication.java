package com.example.uaa;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
@RestController
public class UaaApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }

    @GetMapping("/userinfo")
    public Principal getUserinfo(final Principal principal) {
        return principal;
    }
}
