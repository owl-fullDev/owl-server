package com.owl.owlserver.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class APIUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserCredentials userCredentials = userCredentialsService.findUserCredentialByUsername(username);

        if (userCredentials != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(userCredentials.getRole()));

            return User
                    .withUsername(userCredentials.getUsername())
                    .password(userCredentials.getPassword())
                    .authorities(authorities)
                    .build();
        }
        else {
            throw new UsernameNotFoundException(
                    "User '" + username + "' not found.");
        }
    }
}