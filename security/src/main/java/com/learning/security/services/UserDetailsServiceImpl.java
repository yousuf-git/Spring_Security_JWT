// To work with Spring Security, we need to implement UserDetailsService interface.

package com.learning.security.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.learning.security.repos.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * Get user from DB and convert it into UserDetailsImpl object then return it.
         */
        return userRepo.findByUsername(username)
            .map(UserDetailsImpl::build)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

}
