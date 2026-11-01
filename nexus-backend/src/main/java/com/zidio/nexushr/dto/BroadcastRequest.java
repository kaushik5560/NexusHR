package com.zidio.nexushr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastRequest {
    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message content is required")
    private String content;

    private List<String> channels; // ["EMAIL", "SMS"]
    private String targetAudience; // "ALL", "ENGINEERING", "HR"
}
