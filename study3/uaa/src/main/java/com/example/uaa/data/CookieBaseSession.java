package com.example.uaa.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;

public class CookieBaseSession {

    private static ThreadLocal<Properties> sessions = new ThreadLocal<>();

    public static Properties getSession() {
        Properties session = sessions.get();
        if (session == null) {
            session = new Properties();
            sessions.set(session);
        }
        return session;
    }

    @Provider
    public static class Filter implements ContainerRequestFilter, ContainerResponseFilter {

        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            final Cookie cookie = requestContext.getCookies().get("session");
            if (cookie == null) {
                return;
            }
            final ByteArrayInputStream in = new ByteArrayInputStream(
                    Base64.getDecoder().decode(cookie.getValue()));
            final Properties session = new Properties();
            session.load(in);
            sessions.set(session);
        }

        @Override
        public void filter(final ContainerRequestContext requestContext,
                final ContainerResponseContext responseContext) throws IOException {
            final Properties session = sessions.get();
            if (session == null) {
                return;
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            session.store(out, "");
            final String value = Base64.getEncoder().encodeToString(out.toByteArray());
            responseContext.getHeaders().add("Set-Cookie",
                    new NewCookie("session", value, "/uaa", "localhost", "", 3600, false));
        }
    }
}
