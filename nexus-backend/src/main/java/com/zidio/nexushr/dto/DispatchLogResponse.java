package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.DispatchLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchLogResponse {
    private Long id;
    private String channel;
    private String recipient;
    private String subject;
    private String content;
    private String deliveryStatus;
    private Long latencyMs;
    private LocalDateTime dispatchedAt;

    public static DispatchLogResponse fromEntity(DispatchLog entity) {
        return DispatchLogResponse.builder()
                .id(entity.getId())
                .channel(entity.getChannel())
                .recipient(entity.getRecipient())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .deliveryStatus(entity.getDeliveryStatus())
                .latencyMs(entity.getLatencyMs())
                .dispatchedAt(entity.getDispatchedAt())
                .build();
    }
}
