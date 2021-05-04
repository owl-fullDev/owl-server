package com.owl.owlserver.Controller.HeadOfficeController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('office') or hasRole('admin') or hasRole('boss')")
@CrossOrigin
@RestController
@RequestMapping("/hoEndpoint")
public class HeadOfficeEndpoint {

    //default endpoint
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("This is the head office terminal api, GET request received", HttpStatus.OK);
    }

}