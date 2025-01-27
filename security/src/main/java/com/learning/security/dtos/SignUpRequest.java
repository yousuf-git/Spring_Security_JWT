package com.learning.security.dtos;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @NotNull
    @Size(min = 5, max = 20)
    private String username;
    
    @NotBlank
    @NotNull
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 30)
    @NotNull
    private String password;
    
    
    private Set<String> roles;  // if null will be passed, by default it will be a USER

}

// Dummy JSON for testing:
// {
//   "username": "yousuf",
//   "email": yousuf@gmail.com"
//   "password": "yousuf",
//   "roles": ["admin"]
// }
