package com.example.demo.controller;

import com.example.demo.config.VNPayService;
import com.example.demo.dto.model.GuessResponse;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/guess")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GuessResponse> play(@RequestParam Integer number) {
        var response = gameService.play(number);
        return ResponseEntity.ok(response);
    }
}
