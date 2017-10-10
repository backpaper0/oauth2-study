package com.example.uaa.filter;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

public abstract class BasicAuthFilterSkeleton implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getSecurityContext().getUserPrincipal() != null) {
            return;
        }
        final String header = requestContext.getHeaderString("Authorization");
        if (header != null && header.toLowerCase().startsWith("basic ")) {
            final String[] splitted = new String(
                    Base64.getDecoder().decode(header.substring("Basic ".length()))).split(":");
            final String username = splitted[0];
            final String password = splitted[1];
            final SecurityContext sc = authenticate(username, password);
            if (sc != null) {
                requestContext.setSecurityContext(sc);
                return;
            }
        }
        requestContext.abortWith(Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate",
                "Basic realm=\"Resource Owner\"").build());
    }

    protected abstract SecurityContext authenticate(String username, String password);
}
