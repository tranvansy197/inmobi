package com.example.demo.dto.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GuessResponse {
    private String message;
    private String email;
    private Integer turns;
    private Integer score;
}
