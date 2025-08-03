package com.example.demo.controller;

import com.example.demo.dto.model.LeaderboardEntry;
import com.example.demo.dto.model.PaymentStatus;
import com.example.demo.dto.model.UserInfo;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfo> getUserInfo() {
        var user = userService.getUserInfo();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/leaderboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaderboardEntry>> getTopScoreUsers() {
        var users = userService.getTopScoreUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/buy-turns")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> buyTurns(HttpServletRequest request) {
        String payUrl = userService.buyTurns(request);
        return ResponseEntity.ok(payUrl);
    }

    @GetMapping("/vnpay-payment/{id}")
    public PaymentStatus returnPaymentInfo(@PathVariable Long id, HttpServletRequest request, Model model){
        PaymentStatus paymentStatus = userService.orderReturn(request, id);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus;
    }
}
