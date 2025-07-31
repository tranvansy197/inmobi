package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.dto.model.GuessResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Transactional
public class GameServiceImpl implements GameService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Random random = new Random();

    @Value("${app.probability}")
    private int winProbability;

    private final ConcurrentMap<String, Boolean> concurrentMap = new ConcurrentHashMap<>();

    public GuessResponse play(Integer number) {
        String currentEmail = jwtTokenProvider.getCurrentUser().getEmail();
        if(concurrentMap.putIfAbsent(currentEmail, true) != null) {
            throw new BadRequestException("You are already playing.");
        }
        try {
            if (number < 1 || number > 5) {
                throw new BadRequestException("Invalid number");
            }

            Optional<User> op = userRepository.findByEmail(currentEmail);
            if (op.isEmpty()) {
                throw new BadRequestException("User not found");
            }

            User user = op.get();
            if (user.getTurns() <= 0) {
                throw new BadRequestException("No turn left.");
            }

            user.setTurns(user.getTurns() - 1);

            boolean isWin = number == random.nextInt(5) + 1 &&
                    (random.nextDouble() < (double) winProbability / 100);

            String message = "Better luck next time!";
            if (isWin) {
                user.setScore(user.getScore() + 1);
                message = "You won the prize!";
            }

            userRepository.save(user);

            GuessResponse response = new GuessResponse();
            response.setMessage(message);
            response.setEmail(user.getEmail());
            response.setTurns(user.getTurns());
            response.setScore(user.getScore());
            return response;

        } finally {
            concurrentMap.remove(currentEmail);
        }
    }
}
