package com.owl.owlserver.Security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/usersEndpoint")
public class UsersEndpoint {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    UserCredentialsService userCredentialsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "This is the users endpoint, get request received";
    }

    @PostMapping("/login")
    public ResponseEntity<String> attemptLogin(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();
        
        UserCredentials userCredentials = userCredentialsService.findUserCredentialByUsername(username);

        if (userCredentials!=null){
            if (passwordEncoder.matches(password,userCredentials.getPassword())) {
                return new ResponseEntity<>(userCredentials.getRole(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Wrong username or password",HttpStatus.UNAUTHORIZED);
    }

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
        
