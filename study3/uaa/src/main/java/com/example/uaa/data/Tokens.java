package com.example.uaa.data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Tokens {

    private final ConcurrentMap<String, String> tokens = new ConcurrentHashMap<>();

    public void add(final String token, final String username) {
        tokens.put(token, username);
    }

    public String get(final String token) {
        return tokens.get(token);
    }
}
