package com.capgemini.test.code.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
public class User {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 100)
    private String name;

    @Column (name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column (name = "dni", nullable = false, unique = true, length = 15)
    private String dni;

    @Column (name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column (name = "role", nullable = false, length = 50)
    private String role;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "room_id")
    private Room room;
}
