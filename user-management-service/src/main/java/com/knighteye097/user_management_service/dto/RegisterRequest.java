package com.knighteye097.user_management_service.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Set<String> roles; // pass roles as strings like "ROLE_USER"
}
