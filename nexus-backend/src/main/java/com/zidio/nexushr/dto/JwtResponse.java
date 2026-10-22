package com.zidio.nexushr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String employeeCode;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}