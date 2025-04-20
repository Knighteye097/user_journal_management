package com.knighteye097.user_management_service.dto;

import com.knighteye097.user_management_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String name;
    private String password;
    private Set<Role> roles;
}
