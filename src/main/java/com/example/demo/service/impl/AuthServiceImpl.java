package com.example.demo.service.impl;


import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.model.RoleEnum;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address already in use!");
        }

        Role roleDefault = roleRepository.findByRoleName(RoleEnum.ROLE_USER.name())
                .orElseThrow(()-> new BadRequestException("Role does not exists."));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(roleDefault.getRoleId());

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        String jwt = tokenProvider.generateToken(authentication);


        return new LoginResponse(jwt);
    }
}
