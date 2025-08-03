package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.dto.model.GuessResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedissonClient redissonClient;
    private final Random random = new Random();

    @Value("${app.probability}")
    private int winProbability;

    @Transactional()
    public GuessResponse play(Integer number) {
        String currentEmail = jwtTokenProvider.getCurrentUser().getEmail();
        String lockKey = "lock:guess:user:" + currentEmail;
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked;

        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BadRequestException("The system is processing the request. Please try again!");
            }

            if (number < 1 || number > 5) {
                throw new BadRequestException("Invalid number (1-5).");
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException(e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
