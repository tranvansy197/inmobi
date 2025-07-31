package com.example.demo.security;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.exception.AuthenticationException;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailCustomService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("User not found."));
        Role role = roleRepository.findByRoleId(user.getRoleId())
                .orElseThrow(() -> new AuthenticationException("Role not found."));

        return UserPrincipal.create(user, role.getRoleName());
    }
}
