package com.example.demo.repo;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "select * from users u where u.role_id = 2 order by u.score desc limit 10", nativeQuery = true)
    List<User> getTopScoreUsers();
}
