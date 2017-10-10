package com.example.uaa.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ws.rs.core.SecurityContext;

import com.example.uaa.impl.PrincipalImpl;
import com.example.uaa.impl.SecurityContextImpl;

public class Users {

    private final Collection<User> users = new ArrayList<>();

    public void add(final String username, final String password, final Collection<String> roles) {
        users.add(new User(username, password, roles));
    }

    public SecurityContext getSecurityContext(final String username,
            final String password, final String authenticationScheme) {
        return users.stream()
                .filter(User.authenticator(username, password))
                .findFirst()
                .map(User.securityContextFactory(authenticationScheme))
                .orElse(null);
    }

    private static class User {

        private final String username;
        private final String password;
        private final Collection<String> roles;

        public User(final String username, final String password, final Collection<String> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }

        public static Function<User, SecurityContext> securityContextFactory(
                final String authenticationScheme) {
            return user -> new SecurityContextImpl(new PrincipalImpl(user.username), user.roles,
                    false, authenticationScheme);
        }

        public static Predicate<? super User> authenticator(final String username,
                final String password) {
            return user -> Objects.equals(username, user.username)
                    && Objects.equals(password, user.password);
        }
    }
}
