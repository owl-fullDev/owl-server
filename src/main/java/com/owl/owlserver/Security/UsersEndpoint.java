package com.owl.owlserver.Security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/usersEndpoint")
public class UsersEndpoint {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserCredentialsRepository userCredentialsRepository;
    @Autowired
    UserCredentialsService userCredentialsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "This is the users endpoint, get request received";
    }

    @PreAuthorize("hasRole('admin') or hasRole('boss')")
    @GetMapping("/getAllUsers")
    public List<UserCredentials> getAllUsers(){
        return userCredentialsRepository.findAll();
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

    @PreAuthorize("hasRole('admin') or hasRole('boss')")
    @PostMapping(value = "/newUser")
    public ResponseEntity<String> newUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();
        String role = wholeJSON.get("role").asText();

        if (role.equals("store")||role.equals("warehouse")||role.equals("office")){
            String encodedPassword = passwordEncoder.encode(password);
            UserCredentials userCredentials = new UserCredentials(username, encodedPassword, role);
            userCredentialsRepository.save(userCredentials);
            return new ResponseEntity<>("New user successfully created", HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Role of "+role+" not recognized");
        }
    }

    @PreAuthorize("hasRole('admin') or hasRole('boss')")
    @PostMapping(value = "/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        UserCredentials userCredentials = userCredentialsRepository.findByUsername(username);

        if (userCredentials==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with username of "+username+" not found");
        }
        String role = userCredentials.getRole();
        if (role.equals("admin")||role.equals("boss")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"not enough pauthorithy to delete user with role of: "+role);
        }
        userCredentialsRepository.deleteById(userCredentials.getUserId());

        return new ResponseEntity<>("User successfully deleted", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin') or hasRole('boss')")
    @PostMapping(value = "/modifyUser")
    public ResponseEntity<String> modifyUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();
        String role = wholeJSON.get("role").asText();

        UserCredentials userCredentials = userCredentialsRepository.findByUsername(username);
        if (userCredentials==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with username of "+username+" not found");
        }

        if (role.equals("store")||role.equals("warehouse")||role.equals("office")||role.equals("admin")){
            String encodedPassword = passwordEncoder.encode(password);
            userCredentials.setPassword(encodedPassword);
            userCredentials.setRole(role);
            userCredentialsRepository.save(userCredentials);
            return new ResponseEntity<>("User successfully modified", HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Role of "+role+" not recognized");
        }
    }

    @PreAuthorize("hasRole('boss')")
    @PostMapping(value = "/newAdminUser")
    public ResponseEntity<String> newAdminUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();
        String role = wholeJSON.get("role").asText();

        String encodedPassword = passwordEncoder.encode(password);
        UserCredentials userCredentials = new UserCredentials(username, encodedPassword, role);
        userCredentialsRepository.save(userCredentials);
        return new ResponseEntity<>("New user successfully created", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('boss')")
    @PostMapping(value = "/deleteAdminUser")
    public ResponseEntity<String> deleteAdminUser(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String username = wholeJSON.get("username").asText();

        UserCredentials userCredentials = userCredentialsRepository.findByUsername(username);
        if (userCredentials==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with username of "+username+" not found");
        }
        userCredentialsRepository.deleteById(userCredentials.getUserId());
        return new ResponseEntity<>("User successfully deleted", HttpStatus.OK);
    }
}
        
