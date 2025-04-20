package com.knighteye097.user_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private String type;
    private String email;
    private String name;
    private String message;
    private LocalDateTime timestamp;
}
