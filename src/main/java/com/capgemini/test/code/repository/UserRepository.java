package com.capgemini.test.code.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capgemini.test.code.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
    boolean existsByEmail(String email);

    Optional<User> findByIdAndRoomId(Long id, Long roomId);
}
