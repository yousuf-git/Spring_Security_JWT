// Handle Authentication Exception

package com.learning.security.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.learning.security.exceptions.CustomJwtException;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
@Component
//@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    Logger log = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * This method will be triggerd anytime unauthenticated User requests a secured HTTP resource
     * and an AuthenticationException is thrown
     * @param request that resulted in an <code>AuthenticationException</code>
	 * @param response so that the user agent can begin authentication
	 * @param authException that caused the invocation
     */

//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//            AuthenticationException authException) {
//
//        // log.error("Un-authorization error: {}", authException.getMessage());
//        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized"); // 401
//
//        try {
//            // Customizing the response
//
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//            final Map<String, Object> body = new HashMap<>();
//
//            body.put("Status", HttpServletResponse.SC_UNAUTHORIZED);
//            body.put("Error", "Authentication error !");
//            body.put("Message", authException.getMessage());
//            body.put("Path", request.getServletPath());
//
//            final ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(response.getOutputStream(), body);
//            log.error("Auth Error: {} ", authException.getMessage());
//
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            final Map<String, Object> body = new HashMap<>();
//            body.put("Status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("Path", request.getServletPath());
            // Check for stored JWT exception first
            CustomJwtException jwtException = (CustomJwtException) request.getAttribute("jwt.exception");


//            if (authException.getCause() instanceof CustomJwtException) {
//                CustomJwtException jwtException = (CustomJwtException) authException.getCause();
//                response.setStatus(jwtException.getStatusCode());
//                body.put("Error", jwtException.getMessage());
//            } else {
//                body.put("Error", "Unauthorized!");
//                body.put("Message", authException.getMessage());
//            }
            if (jwtException != null) {
                response.setStatus(jwtException.getStatusCode());
                body.put("Status", jwtException.getStatusCode());
                body.put("Error", jwtException.getMessage());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                body.put("Status", HttpServletResponse.SC_UNAUTHORIZED);
                body.put("Error", "Unauthorized!");
                body.put("Message", authException.getMessage());
            }

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
            log.error("Auth Error: {} ", authException.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
