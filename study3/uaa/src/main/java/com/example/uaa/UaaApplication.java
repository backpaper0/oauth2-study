package com.example.uaa;

import java.net.URI;
import java.util.Arrays;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.example.uaa.api.OAuthApi;
import com.example.uaa.api.UserinfoApi;
import com.example.uaa.data.CookieBaseSession;
import com.example.uaa.data.OAuthClients;
import com.example.uaa.data.Tokens;
import com.example.uaa.data.Users;
import com.example.uaa.filter.OAuthClientBasicAuthFilter;
import com.example.uaa.filter.ResourceOwnerBasicAuthFilter;
import com.example.uaa.filter.ResourceSecurityFilter;

public class UaaApplication {

    public static void main(final String[] args) {
        final URI uri = URI.create("http://localhost:9999/uaa");
        final ResourceConfig configuration = new ResourceConfig();

        final Tokens tokens = new Tokens();

        final Users resourceOwners = new Users();
        resourceOwners.add("uragami", "ilovejava", Arrays.asList("USERS"));

        final OAuthClients oauthClients = new OAuthClients();
        oauthClients.add("helloworld", "hellosecret", Arrays.asList("read"),
                "http://localhost:8080/login");

        configuration.register(new OAuthApi(tokens, oauthClients));
        configuration.register(new ResourceOwnerBasicAuthFilter(resourceOwners));
        configuration.register(new OAuthClientBasicAuthFilter(oauthClients));
        configuration.register(UserinfoApi.class);
        configuration.register(new ResourceSecurityFilter(tokens));
        configuration.register(CookieBaseSession.Filter.class);

        GrizzlyHttpServerFactory.createHttpServer(uri, configuration);
    }
}
