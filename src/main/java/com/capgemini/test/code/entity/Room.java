package com.capgemini.test.code.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "rooms")
@Getter 
@Setter 
@NoArgsConstructor 
public class Room {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, unique = true, length = 100)
    private String name;
}
