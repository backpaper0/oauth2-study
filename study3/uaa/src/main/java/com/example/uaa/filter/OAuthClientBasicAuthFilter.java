package com.example.uaa.filter;

import java.util.Objects;

import javax.ws.rs.core.SecurityContext;

import com.example.uaa.annotation.OAuthClientSecurity;
import com.example.uaa.data.OAuthClients;

@OAuthClientSecurity
public class OAuthClientBasicAuthFilter extends BasicAuthFilterSkeleton {

    private final OAuthClients clients;

    public OAuthClientBasicAuthFilter(final OAuthClients clients) {
        this.clients = Objects.requireNonNull(clients);
    }

    @Override
    protected SecurityContext authenticate(final String username, final String password) {
        return clients.getSecurityContext(username, password, SecurityContext.BASIC_AUTH);
    }
}
