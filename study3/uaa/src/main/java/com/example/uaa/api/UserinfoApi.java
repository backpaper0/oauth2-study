package com.example.uaa.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.example.uaa.annotation.OAuthResourceSecurity;

@Path("/userinfo")
public class UserinfoApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @OAuthResourceSecurity
    public String get(@Context final SecurityContext sc) {
        return "{\"name\":\"" + sc.getUserPrincipal().getName() + "\"}";
    }
}
