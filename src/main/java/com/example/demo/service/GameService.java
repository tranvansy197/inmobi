package com.example.demo.service;

import com.example.demo.dto.model.GuessResponse;

public interface GameService {
    GuessResponse play(Integer number);
}
