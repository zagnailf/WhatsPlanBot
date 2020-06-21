package ru.whatsplan.bot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleRestController {

    @Value("telegram.bot.username")
    private String username;

    @RequestMapping(value = "/bot/username", method = RequestMethod.GET)
    public ResponseEntity<?> botUsername() {
        return ResponseEntity.ok(username);
    }
}
