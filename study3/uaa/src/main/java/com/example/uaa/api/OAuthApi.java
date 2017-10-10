package com.example.uaa.api;

import java.net.URI;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import com.example.uaa.annotation.OAuthClientSecurity;
import com.example.uaa.annotation.ResourceOwnerSecurity;
import com.example.uaa.data.CookieBaseSession;
import com.example.uaa.data.OAuthClients;
import com.example.uaa.data.Tokens;

@Path("/oauth")
public class OAuthApi {

    private final Tokens tokens;
    private final OAuthClients oauthClients;

    private final ConcurrentMap<String, String> code2tokens = new ConcurrentHashMap<>();

    public OAuthApi(final Tokens tokens, final OAuthClients oauthClients) {
        this.tokens = Objects.requireNonNull(tokens);
        this.oauthClients = Objects.requireNonNull(oauthClients);
    }

    @GET
    @Path("/authorize")
    @ResourceOwnerSecurity
    public Response get(
            @QueryParam("client_id") final String clientId,
            @QueryParam("redirect_uri") final String redirectUri,
            @QueryParam("response_type") final String responseType,
            @QueryParam("state") final String state,
            @Context final SecurityContext sc) {

        oauthClients.validate(clientId, redirectUri);

        if (responseType.equals("code") == false) {
            throw new BadRequestException();
        }

        final Properties session = CookieBaseSession.getSession();
        final boolean approval = Boolean.parseBoolean(session.getProperty(clientId + ".approval"));
        if (approval) {
            final String token = UUID.randomUUID().toString();
            tokens.add(token, sc.getUserPrincipal().getName());
            final String code = UUID.randomUUID().toString();
            code2tokens.put(code, token);
            final URI location = UriBuilder.fromUri(redirectUri)
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .build();
            return Response.status(Status.FOUND).location(location).build();
        }

        session.setProperty(clientId + ".state", state);

        final String body = "<!DOCTYPE html><html><head><title>authorize</title></head><body>"
                + "<form action='/uaa/oauth/authorize' method='POST'>"
                + "scope.read"
                + "<label><input type='radio' name='approval' value='true'> Approve</label>"
                + "<label><input type='radio' name='approval' value='false'> Deny</label>"
                + "<button>Submit</button>"
                + "<input type='hidden' name='client_id' value='" + clientId + "'>"
                + "</form>"
                + "</body></html>";

        return Response.ok(body).build();
    }

    @POST
    @Path("/authorize")
    @ResourceOwnerSecurity
    public Response post(
            @FormParam("client_id") final String clientId,
            @FormParam("approval") final boolean approval,
            @Context final SecurityContext sc) {

        final Properties session = CookieBaseSession.getSession();

        final String redirectUri = oauthClients.getRedirectUri(clientId);
        final String state = session.getProperty(clientId + ".state");
        if (approval) {
            final String token = UUID.randomUUID().toString();
            tokens.add(token, sc.getUserPrincipal().getName());
            final String code = UUID.randomUUID().toString();
            code2tokens.put(code, token);
            final URI location = UriBuilder.fromUri(redirectUri)
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .build();
            return Response.status(Status.FOUND).location(location).build();
        }

        final String body = "<!DOCTYPE html><html><head><title>authorize</title></head><body>"
                + "<p>Denied</p>"
                + "</body></html>";

        return Response.ok(body).build();
    }

    @POST
    @Path("/token")
    @OAuthClientSecurity
    public Response token(@FormParam("code") final String code) {
        final String token = code2tokens.remove(code);
        if (token == null) {
            throw new BadRequestException();
        }
        final String json = "{\"access_token\":\"" + token
                + "\",\"token_type\":\"bearer\",\"expires_in\":3600,\"scope\":\"read\"}";
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
}
