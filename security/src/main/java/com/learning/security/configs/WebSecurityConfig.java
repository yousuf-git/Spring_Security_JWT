package com.learning.security.configs;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learning.security.auth.AuthEntryPointJwt;
import com.learning.security.auth.AuthTokenFilter;
import com.learning.security.auth.JwtAccessDeniedHandler;
import com.learning.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    // @Autowired
    // JwtUtils jwtUtils;
    
    @Bean
    public AuthTokenFilter getAuthTokenFilter() {
        return new AuthTokenFilter();
        // return new AuthTokenFilter(jwtUtils);
    }

    @Bean
    public DaoAuthenticationProvider getAuthProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Setting password encoder in auth provider
        authProvider.setPasswordEncoder(passwordEncoder());
        // Setting user details service in auth provider
        authProvider.setUserDetailsService(userDetailsServiceImpl);

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager getAuthManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    AuthEntryPointJwt authEntryPointJwt;
    @Autowired
    JwtAccessDeniedHandler accessDeniedHandler;
    
    @Bean 
    SecurityFilterChain getFilterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling((e) -> {
                    e.authenticationEntryPoint(authEntryPointJwt);
                    e.accessDeniedHandler(accessDeniedHandler);
                } 
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // ____________________________________
            // .authenticationProvider(getAuthProvider())
            // .addFilterBefore(getAuthTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            // ____________________________________
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/test/all/**").permitAll()
                    .requestMatchers("/greet/**").permitAll()
                    // For swagger docs
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v2/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                    .anyRequest().authenticated();
            });
            http.authenticationProvider(getAuthProvider());
            http.addFilterBefore(getAuthTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    
}

/**
 * For CORS config

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins("http://domain2.com")
			.allowedMethods("PUT", "DELETE")
			.allowedHeaders("header1", "header2", "header3")
			.exposedHeaders("header1", "header2")
			.allowCredentials(false).maxAge(3600);
	}
}

*/