// Auth Controller

package com.learning.security.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.security.dtos.JwtResponse;
import com.learning.security.dtos.LoginRequest;
import com.learning.security.dtos.ResponseMessage;
import com.learning.security.dtos.SignUpRequest;
import com.learning.security.enums.ERole;
import com.learning.security.models.Role;
import com.learning.security.models.User;
import com.learning.security.repos.RoleRepo;
import com.learning.security.repos.UserRepo;
import com.learning.security.services.UserDetailsImpl;
import com.learning.security.utils.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600 )         // 3600 sec = 1 hr
/*
 * The HTTP Access-Control-Max-Age response header indicates how long the results of a preflight request (that is, the information contained in the Access-Control-Allow-Methods and Access-Control-Allow-Headers headers) can be cached.
 */
// https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/cors.html
public class AuthController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder encoder; 
    // Will automatically find its implemenation from <code>WebSecurityConfig<code> which is BCryptPasswordEncoder

    @Autowired
    RoleRepo roleRepo;

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody SignUpRequest request) {

        // Check if user already exists - by username
        if (userRepo.existsByUsername(request.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Username already exists !"), HttpStatus.BAD_REQUEST);
        }

        // Check if user already exists - by email
        if (userRepo.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Email already exists !"), HttpStatus.BAD_REQUEST);
        }

        // If not - Build a new user
        User user = new User();
        // 
        /**
         * --------------- <code>User<code> requires ---------------
         * String username
         * String email
         * String password - first encode and then attach with user
         * Set<Role> roles
         */
        // Trying to grab the roles from request and set them to user
        if (!setRoles(user, request.getRoles())) {
            return new ResponseEntity<>(new ResponseMessage("No Valid role found in the request !"), HttpStatus.BAD_REQUEST);
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        userRepo.save(user);

        return new ResponseEntity<>(new ResponseMessage("User Created Successfully :)"), HttpStatus.CREATED);
    }

    /**
     * 
     * @param user
     * @param rolesFromReq
     * @return              false if no valid role found in rolesFromReq, true otherwise
     * @apiNote             All defined Roles Must exist in the DB already
     */
    
    private boolean setRoles(User user, Set<String> rolesFromReq) {

        // Default Role - USER
        if (rolesFromReq == null) {
            // Set<Role> roles = new HashSet<>();
            // Role role = new Role();
            // role.setName(ERole.USER);
            // roles.add(role);
            // user.setRoles(roles);
            // Concising the above code    
            user.setRoles(Set.of(Role.builder().name(ERole.USER).build()));
            return true;
        }

        // Get all roles from Set<String> roles
        // create a Set<Role> and attach that to user
        
        Set<Role> roles = new HashSet<>(); // for user
        // Set<ERole> roleEnums = new HashSet<>();
        
        for (String roleStr : rolesFromReq) {
            switch (roleStr) {
                case "user":
                    Role userRole = roleRepo.findByName(ERole.USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found in DB."));
                    roles.add(userRole);
                    // roles.add(Role.of(ERole.USER));
                    break;
                case "admin":
                    Role adminRole = roleRepo.findByName(ERole.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found in DB."));
                    roles.add(adminRole);
                    // roles.add(Role.of(ERole.ADMIN));
                    break;
                case "mod":
                    Role modRole = roleRepo.findByName(ERole.MODERATOR)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found in DB."));
                    roles.add(modRole);
                    // roles.add(Role.of(ERole.MODERATOR));
                    break;
                default:
                    break;
            }
        }
        
        if (roles.isEmpty()) {
            return false;           // No valid role found in the signup request
        }

        user.setRoles(roles);
        return true;
    }

    @Autowired
    AuthenticationManager authManager;

    @Autowired 
    JwtUtils jwtUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signin(@Valid @RequestBody LoginRequest loginRequest) {

        // Authenticating the user using AuthenticationManager
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        // UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken which implements Authentication

        // Throws: AuthenticationException - if authentication fails

        // Setting in the security context holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Now comes the JWT stuff
        String jwt = jwtUtils.generateTokenByAuth(authentication);

        // Now we have to construct the JwtResponse from the UserDetailsImpl

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetailsImpl.getRoles().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetailsImpl.getId(), userDetailsImpl.getUsername(), userDetailsImpl.getEmail(), roles);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

}
