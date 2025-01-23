// This is a controller class that is used to greet the user and test the application.

package com.learning.security.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController("greet")
public class greet {
    
    @GetMapping("/")
    public String hi() {
        return "Hello Yousuf";
    }
}
