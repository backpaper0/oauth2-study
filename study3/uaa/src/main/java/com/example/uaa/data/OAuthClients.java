package com.example.uaa.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import com.example.uaa.impl.PrincipalImpl;
import com.example.uaa.impl.SecurityContextImpl;

public class OAuthClients {

    private final Collection<OAuthClient> oauthClients = new ArrayList<>();

    public void add(final String clientId, final String clientSecret,
            final Collection<String> scopes,
            final String redirectUri) {
        oauthClients.add(new OAuthClient(clientId, clientSecret, scopes, redirectUri));
    }

    public SecurityContext getSecurityContext(final String username, final String password,
            final String authenticationScheme) {
        return oauthClients.stream()
                .filter(OAuthClient.authenticator(username, password))
                .findFirst()
                .map(OAuthClient.securityContextFactory(authenticationScheme))
                .orElse(null);
    }

    public void validate(final String clientId, final String redirectUri) {
        oauthClients.stream()
                .filter(OAuthClient.validate(clientId, redirectUri))
                .findAny()
                .orElseThrow(BadRequestException::new);
    }

    public String getRedirectUri(final String clientId) {
        return oauthClients.stream()
                .filter(client -> Objects.equals(client.clientId, clientId))
                .map(client -> client.redirectUri)
                .findAny()
                .orElseThrow(BadRequestException::new);
    }

    private static class OAuthClient {

        private final String clientId;
        private final String clientSecret;
        private final Collection<String> scopes;
        private final String redirectUri;

        public OAuthClient(final String clientId, final String clientSecret,
                final Collection<String> scopes,
                final String redirectUri) {
            this.clientId = Objects.requireNonNull(clientId);
            this.clientSecret = Objects.requireNonNull(clientSecret);
            this.scopes = Objects.requireNonNull(scopes);
            this.redirectUri = Objects.requireNonNull(redirectUri);
        }

        public static Predicate<? super OAuthClient> validate(final String clientId,
                final String redirectUri) {
            return client -> Objects.equals(client.clientId, clientId)
                    && Objects.equals(client.redirectUri, redirectUri);
        }

        public static Function<OAuthClient, SecurityContext> securityContextFactory(
                final String authenticationScheme) {
            return client -> new SecurityContextImpl(new PrincipalImpl(client.clientId),
                    client.scopes,
                    false, authenticationScheme);
        }

        public static Predicate<? super OAuthClient> authenticator(final String clientId,
                final String clientSecret) {
            return client -> Objects.equals(clientId, client.clientId)
                    && Objects.equals(clientSecret, client.clientSecret);
        }
    }
}
