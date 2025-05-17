package com.service.document.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreatedEvent {
    private Long documentId;
    private Long userId;
    private Instant timestamp;
}