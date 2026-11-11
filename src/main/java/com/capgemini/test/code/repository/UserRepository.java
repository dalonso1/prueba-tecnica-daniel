package com.capgemini.test.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capgemini.test.code.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
    boolean exexistsByEmail(String email);
}
