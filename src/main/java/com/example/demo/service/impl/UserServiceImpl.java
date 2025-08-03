package com.example.demo.service.impl;

import com.example.demo.config.VNPayService;
import com.example.demo.domain.User;
import com.example.demo.dto.model.LeaderboardEntry;
import com.example.demo.dto.model.PaymentStatus;
import com.example.demo.dto.model.UserInfo;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final VNPayService vnPayService;
    private final RedissonClient redissonClient;
    private final static String LEADERBOARD_KEY = "leaderboard";

    @Value("${app.turn}")
    private int turn;

    @Value("${app.price}")
    private int price;

    @Value("${app.domainUrl}")
    private String domainUrl;

    public UserInfo getUserInfo() {
        var user = jwtTokenProvider.getCurrentUser().getUser();
        return toUserInfo(user);
    }

    public List<LeaderboardEntry> getTopScoreUsers() {
        RScoredSortedSet<String> leaderBoardSet = redissonClient.getScoredSortedSet(LEADERBOARD_KEY);
        Collection<ScoredEntry<String>> entries = leaderBoardSet.entryRangeReversed(0, 9);

        return entries.stream()
                .map(e -> new LeaderboardEntry(e.getValue(), e.getScore()))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<UserInfo> getTopScoreUsers() {
//        List<User> users = userRepository.getTopScoreUsers();
//        return users.stream().map(this::toUserInfo).toList();
//    }

    private UserInfo toUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(user.getEmail());
        userInfo.setTurns(user.getTurns());
        userInfo.setScore(user.getScore());
        return userInfo;
    }

    @Override
    public String buyTurns(HttpServletRequest request) {
        var user = jwtTokenProvider.getCurrentUser().getUser();
        return vnPayService.createPayment(turn * price, "Purchase your turns.", domainUrl, user.getUserId());
    }

    @Override
    public PaymentStatus orderReturn(HttpServletRequest request, Long id) {
        var user = userRepository.findByUserId(id);
        if (user == null) {
            return PaymentStatus.PAYMENT_FAILED;
        }
        int paymentStatus = vnPayService.orderReturn(request);
        if (paymentStatus == 1) {
            RScoredSortedSet<String> leaderBoardSet = redissonClient.getScoredSortedSet(LEADERBOARD_KEY);
            leaderBoardSet.addScore(user.getEmail(), 1);
            user.setTurns(user.getTurns() + turn);
            userRepository.save(user);
            return PaymentStatus.PAYMENT_SUCCESS;
        }

        return PaymentStatus.PAYMENT_FAILED;
    }
}
