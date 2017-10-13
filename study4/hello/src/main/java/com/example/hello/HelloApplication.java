package com.example.hello;

import java.net.URI;
import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloApplication {

    public static void main(final String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

    @GetMapping
    String get(final Principal principal) {
        return String.format("Hello, %1$s!", principal.getName());
    }
}

@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @ConfigurationProperties(prefix = "hello")
    ClientRegistrationProperties clientRegistrationProperties() {
        return new ClientRegistrationProperties();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()

                .and()
                .oauth2Login()
                .clients(new ClientRegistration.Builder(clientRegistrationProperties()).build())
                .userInfoEndpoint()
                .userNameAttributeName("name",
                        URI.create(clientRegistrationProperties().getUserInfoUri()));
    }
}