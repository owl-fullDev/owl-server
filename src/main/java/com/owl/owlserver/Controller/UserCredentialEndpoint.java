package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.Security.UserCredentials;
import com.owl.owlserver.Security.UserCredentialsRepository;
import com.owl.owlserver.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/userCredentialAPI")
public class UserCredentialEndpoint {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @PostMapping(value = "/newUser")
    public void newUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();
        String role = wholeJSON.get("role").asText();

        String encodedPassword = passwordEncoder.encode(password);
        UserCredentials userCredentials = new UserCredentials(username, encodedPassword, role);
        userCredentialsRepository.save(userCredentials);

    }
}
