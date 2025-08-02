package com.example.aichat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChattingTest")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private Long chatId;
    
    @Column
    private Long memberId;

    @Lob
    private String message;

    @Column(nullable = false)
    private LocalDateTime createAt;
    
    @Column
    private String chatType;

    @Column(nullable = false, length = 10)
    private String answerType;
    
    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
