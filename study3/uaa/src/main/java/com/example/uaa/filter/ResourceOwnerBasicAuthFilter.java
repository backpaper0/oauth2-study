package com.example.uaa.filter;

import java.util.Objects;

import javax.ws.rs.core.SecurityContext;

import com.example.uaa.annotation.ResourceOwnerSecurity;
import com.example.uaa.data.Users;

@ResourceOwnerSecurity
public class ResourceOwnerBasicAuthFilter extends BasicAuthFilterSkeleton {

    private final Users users;

    public ResourceOwnerBasicAuthFilter(final Users users) {
        this.users = Objects.requireNonNull(users);
    }

    @Override
    protected SecurityContext authenticate(final String username, final String password) {
        return users.getSecurityContext(username, password, SecurityContext.BASIC_AUTH);
    }
}
