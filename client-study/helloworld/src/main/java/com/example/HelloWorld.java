package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.json.Json;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 8080);
        int backlog = 0; //use default value
        HttpServer server = HttpServer.create(addr, backlog);

        HttpContext context = server.createContext("/");

        AtomicReference<String> accessToken = new AtomicReference<>();

        Authenticator authenticator = new Authenticator() {

            @Override
            public Result authenticate(HttpExchange exchange) {
                if (accessToken.get() == null) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null) {
                        String code = Arrays.stream(query.split("&"))
                                .map(x -> x.split("="))
                                .collect(Collectors.toMap(x -> x[0], x -> x[1]))
                                .get("code");
                        if (code != null) {
                            try {

                                Map<String, String> map = new HashMap<>();
                                map.put("grant_type", "authorization_code");
                                map.put("redirect_uri", "http://localhost:8080/");
                                map.put("code", code);

                                List<String> list = new ArrayList<>();
                                for (String key : map.keySet()) {
                                    String value = map.get(key);
                                    list.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                                }
                                byte[] content = list.stream().collect(Collectors.joining("&"))
                                        .getBytes();
                                URL url = new URL("http://localhost:9999/uaa/oauth/token");
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                con.setDoInput(true);
                                con.setRequestMethod("POST");
                                con.setFixedLengthStreamingMode(content.length);
                                con.setDoOutput(true);
                                con.setRequestProperty("Authorization", "Basic "
                                        + Base64.getEncoder()
                                                .encodeToString("study:study".getBytes()));
                                con.setRequestProperty("Content-Type",
                                        "application/x-www-form-urlencoded");
                                try (OutputStream out = con.getOutputStream()) {
                                    out.write(content);
                                    out.flush();
                                }

                                con.getResponseCode();

                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                try (InputStream in = con.getInputStream()) {
                                    int i;
                                    byte[] b = new byte[1024];
                                    while (-1 != (i = in.read(b))) {
                                        out.write(b, 0, i);
                                    }
                                }
                                accessToken.set(
                                        Json.createReader(
                                                new ByteArrayInputStream(out.toByteArray()))
                                                .readObject()
                                                .getString("access_token"));

                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                    }
                }

                if (accessToken.get() != null) {
                    try {
                        URL url = new URL("http://localhost:9999/uaa/user");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Authorization", "Bearer " + accessToken.get());

                        con.getResponseCode();

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        try (InputStream in = con.getInputStream()) {
                            int i;
                            byte[] b = new byte[1024];
                            while (-1 != (i = in.read(b))) {
                                out.write(b, 0, i);
                            }
                        }

                        String username = Json.createReader(
                                new ByteArrayInputStream(out.toByteArray()))
                                .readObject()
                                .getJsonObject("principal")
                                .getString("username");

                        HttpPrincipal principal = new HttpPrincipal(username, "study");
                        return new Success(principal);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }

                exchange.getResponseHeaders().add("location",
                        "http://localhost:9999/uaa/oauth/authorize?response_type=code&redirect_uri=http://localhost:8080/&client_id=study");
                return new Failure(302);
            }
        };
        context.setAuthenticator(authenticator);

        context.setHandler(exchange -> {

            exchange.getResponseHeaders().add("Content-Type", "text/plain");

            int statusCode = 200;
            long contentLength = 0;
            exchange.sendResponseHeaders(statusCode, contentLength);

            try (PrintWriter out = new PrintWriter(exchange.getResponseBody())) {
                out.printf("Hello, %s!", exchange.getPrincipal().getUsername());
            }
        });

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        Thread.sleep(Long.MAX_VALUE);
    }
}
