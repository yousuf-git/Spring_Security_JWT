// Handle Authentication Exception

package com.learning.security.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * This method will be triggerd anytime unauthenticated User requests a secured HTTP resource 
     * and an AuthenticationException is thrown
     * @param request that resulted in an <code>AuthenticationException</code>
	 * @param response so that the user agent can begin authentication
	 * @param authException that caused the invocation
     */
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) {
        
        // log.error("Unauthorization error: {}", authException.getMessage());
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized"); // 401

        try {
            // Customizing the response

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();

        body.put("Status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("Error", "Unauthorized !");
        body.put("Message", authException.getMessage());
        body.put("Path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
        log.error(authException.getMessage());
            
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        

        
    }
}
