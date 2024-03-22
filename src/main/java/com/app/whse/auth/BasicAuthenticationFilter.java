package com.app.whse.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Base64;

@Provider
public class BasicAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        
        String authorizationHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);

        
        if (authorizationHeader != null && authorizationHeader.startsWith(BASIC_AUTH_PREFIX)) {
            
            String base64Credentials = authorizationHeader.substring(BASIC_AUTH_PREFIX.length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            
            String[] splitCredentials = credentials.split(":");
            String username = splitCredentials[0];
            String password = splitCredentials[1];

            
            if (isValidCredentials(username, password)) {
               
                return;
            }
        }

        // Authentication failed, send 401 Unauthorized response
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    private boolean isValidCredentials(String username, String password) {
        if("admin".equals(username) && "admin".equals(password)) {
        	return true;
        }else {
        	return false;
        }
    }
}

