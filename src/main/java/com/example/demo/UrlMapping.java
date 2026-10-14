package com.example.demo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Getter
@Setter
@NoArgsConstructor
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10)
    private String shortCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    private long clickCount = 0;
}