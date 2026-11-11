package com.capgemini.test.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capgemini.test.code.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{
    
}
