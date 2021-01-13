package com.owl.owlserver.Controller.HeadOfficeController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/hoEndpoint")
public class HeadOfficeEndpoint {

    //default endpoint
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "This is the head office terminal api, GET request received";
    }

}