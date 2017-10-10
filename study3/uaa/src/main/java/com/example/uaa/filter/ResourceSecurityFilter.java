package com.example.uaa.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.example.uaa.annotation.OAuthResourceSecurity;
import com.example.uaa.data.Tokens;
import com.example.uaa.impl.PrincipalImpl;
import com.example.uaa.impl.SecurityContextImpl;

@OAuthResourceSecurity
public class ResourceSecurityFilter implements ContainerRequestFilter {

    private final Tokens tokens;

    public ResourceSecurityFilter(final Tokens tokens) {
        this.tokens = Objects.requireNonNull(tokens);
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getSecurityContext().getUserPrincipal() != null) {
            return;
        }
        final String header = requestContext.getHeaderString("Authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            final String token = header.substring("bearer ".length());
            final String username = tokens.get(token);
            if (username != null) {
                final Principal userPrincipal = new PrincipalImpl(username);
                final Collection<String> roles = Arrays.asList("OAUTH");
                final boolean secure = false;
                final String authenticationScheme = "OAuth Token";
                requestContext.setSecurityContext(new SecurityContextImpl(userPrincipal, roles,
                        secure, authenticationScheme));
                return;
            }
        }
        requestContext.abortWith(Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate",
                "Basic realm=\"OAuth Resource\"").build());
    }
}
