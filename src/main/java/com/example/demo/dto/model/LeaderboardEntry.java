package com.example.demo.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardEntry {
    private String email;
    private double score;
}
