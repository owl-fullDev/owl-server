package com.owl.owlserver.Controller;

import com.owl.owlserver.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/tapirRestApi")
public class Tapir {

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Why hello there";
    }

    @GetMapping("/i")
    public String i() {
        return "Happy birthday Ishnaaaaaaaaa";
    }

    @GetMapping("/j")
    public String j() {
        return "E-GIRL FEGGATZ DETECTED";
    }

    @GetMapping("/p")
    public String p() {
        return "wassup lil bitch";
    }

    @GetMapping("/s")
    public String s() {
        return "apa lu buka2 sembarangan";
    }

    @GetMapping("/b")
    public String b() {
        return "Happy birthday broooooooooooo";
    }
}
