package com.example.uaa;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.debug(true);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("uragami").password("ilovejava").roles("USER")
                .and().withUser("admin").password("admin").roles("USER", "ADMIN");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .requestMatchers()
                .antMatchers("/oauth/**", "/login")

                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/oauth/**").authenticated()

                .and()
                .formLogin();
    }
}
