/**
 * To work with Spring Security and Authentication object later
 */

package com.learning.security.services;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.learning.security.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;    // It is used to ensure that the class sending the object is the same as the class receiving the object.

    private Integer id;
    private String  username;
    private String  email;
    private String  password;
    private boolean active;
    
    private List<GrantedAuthority>  roles;
    /**
    * You can notice that I've converted Set<Role> into List<GrantedAuthority>.
    * It is important to work with Spring Security and Authentication object later.
    * <p>
    * GrantedAuthority is an interface that represents an authority granted to an Authentication object.
    * It is used to define the authorities granted to users.
        
    * We have SimpleGrantedAuthority class that implements GrantedAuthority interface. 
    */

    public static UserDetailsImpl build(User user) {
    
    // Get the Set<roles> of user and convert it into List<GrantedAuthority>
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(), 
        user.getUsername(), 
        user.getEmail(),
        user.getPassword(),
        true, 
        authorities);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
}
