package com.learning.security.auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.learning.security.services.UserDetailsServiceImpl;
import com.learning.security.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// @Component
public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	JwtUtils jwtUtils;

	// private final JwtUtils jwtUtils;

    // Constructor injection
    // @Autowired
	// public AuthTokenFilter(JwtUtils jwtUtils) {
    //     this.jwtUtils = jwtUtils;
    // }

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
			
		try {
			String jwtToken = parseJwtFromRequest(request);
		
		if (jwtToken != null && jwtUtils.validateJwt(jwtToken)) {
			String username = jwtUtils.getUsernameFromJwtToken(jwtToken);

			UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

			// Setting the current UserDetails in SecurityContext using
			// https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-securitycontext

			UsernamePasswordAuthenticationToken authentication = 
			new UsernamePasswordAuthenticationToken(
					userDetails,                        // principal
					null,                   // password
					userDetails.getAuthorities());      // GrantedAuthorities

			/**
			 *  Setting additional details about the authentication process
			 *  (such as the IP address and session ID) to the authentication object.
			 *  new WebAuthenticationDetailsSource().buildDetails(request) extracts these details from the current HTTP request and attaches them to the authentication object.
			 *  Essentially, it enriches the authentication object with request-specific information.
			 */
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			/**
			 * After this, everytime you want to get UserDetails, just use SecurityContext like this:
			 * UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			 * And you can get the username, password and authorities like this:
			 * userDetails.getUsername()
			 * userDetails.getPassword()
			 * userDetails.getAuthorities()
			*/
				
			} 
		} catch (Exception e) {
			logger.error("Cannot set user Authentication: {}", e);
		}

		// Pass the request to next filter
		filterChain.doFilter(request, response);
			
	}

	private String parseJwtFromRequest(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		/**
		 * Sample
		 * Authorization: Bearer 
		 */

		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
				
			// Skipping "Bearer " and grab the actual token
			return authHeader.substring(7);
		}
		return null;
	}

}
