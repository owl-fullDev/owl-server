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
        return "Happy birthday Ishnaaaaaaaaa\n\n" +
                "https://tenor.com/view/taylor-swift-22-lyrics-gif-12372675";
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

//    //to set custom field in deserializing
//    @GetMapping("/getProduct")
//    public String getProduct(@RequestParam String productId){
//        Product product = productService.getById(productId);
//        JsonNode node = objectMapper.convertValue(product, JsonNode.class);
//        ((ObjectNode)node).put("customVariable", "somethingsomething");
//        return node.toString();
//    }

    //to throw proper errors:
//    orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Sale with specified ID exists"));
}
