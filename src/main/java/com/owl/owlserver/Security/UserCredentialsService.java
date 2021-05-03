package com.owl.owlserver.Security;

import com.owl.owlserver.Security.UserCredentials;
import com.owl.owlserver.Security.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class UserCredentialsService {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Cacheable(value = "userCredentials")
    public UserCredentials findUserCredentialByUsername(String username)
    {
        return userCredentialsRepository.findByUsername(username);
    }

}
