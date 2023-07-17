package com.vti.threadsmeta.controller;

import com.vti.threadsmeta.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Object> getUserInfo(@PathVariable String username) throws IOException {
        return ResponseEntity.ok(userService.getByUsername(username));
    }

    @GetMapping("/{username}/media/download")
    public ResponseEntity<Object> downloadImage(HttpServletResponse response, @PathVariable String username) throws IOException {
        return ResponseEntity.ok(userService.downloadImage(response, username));
    }

}