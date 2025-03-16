package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "log")
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime requestTime;

    public Log(Long userId, LocalDateTime requestTime) {
        this.userId = userId;
        this.requestTime = requestTime;
    }

}
