package com.service.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column
    private String translatedTitle;
    
    @Column(nullable = false)
    private String s3Key;
    
    @Column
    private String contentType;
    
    @Column
    private Long fileSize;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    
    @Column(nullable = false)
    private Long departmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public void setDescription(Object description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDescription'");
    }
}