package com.example.uaa.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.Objects;

import javax.ws.rs.core.SecurityContext;

public class SecurityContextImpl implements SecurityContext {

    private final Principal userPrincipal;
    private final Collection<String> roles;
    private final boolean secure;
    private final String authenticationScheme;

    public SecurityContextImpl(
            final Principal userPrincipal,
            final Collection<String> roles,
            final boolean secure,
            final String authenticationScheme) {
        this.userPrincipal = Objects.requireNonNull(userPrincipal);
        this.roles = Objects.requireNonNull(roles);
        this.secure = secure;
        this.authenticationScheme = Objects.requireNonNull(authenticationScheme);
    }

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    @Override
    public boolean isUserInRole(final String role) {
        return roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return authenticationScheme;
    }
}
