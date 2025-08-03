package com.example.demo.service;

import com.example.demo.dto.model.LeaderboardEntry;
import com.example.demo.dto.model.PaymentStatus;
import com.example.demo.dto.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    UserInfo getUserInfo();

    List<LeaderboardEntry> getTopScoreUsers();

    String buyTurns(HttpServletRequest request);

    PaymentStatus orderReturn(HttpServletRequest request, Long id);
}
