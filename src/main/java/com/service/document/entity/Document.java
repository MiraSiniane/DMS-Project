package com.service.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titleEn;
    private String titleEs;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    public Instant getUpdatedAt() {
    return updatedAt;
}

    public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
}
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ElementCollection
    @CollectionTable(name = "document_collections", 
                    joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "collection_id")
    private Collection<Long> collectionIds;
}