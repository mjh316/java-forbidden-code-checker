package me.jh316.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.jh316.Main;

@RestController
public class CheckingServiceController {

    @GetMapping("/check")
    public String check() {
        try {
            return Main.hasForbidden("P1");
        } catch (Exception e) {
            return "Error";
        }
    }
}
