package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.Shipment;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Login")
public class HOLogin {

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("This is the login endpoint, GET request successfully received", HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<String> login(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        String username = wholeJSON.get("username").asText();
        String password = wholeJSON.get("password").asText();

        //login functionality
        //start session.
        //timeout after 12 hrs or 00:00

        return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<String> logout()  {

        //logout functionality

        return new ResponseEntity<>("Successfully logged out", HttpStatus.OK);
    }
}