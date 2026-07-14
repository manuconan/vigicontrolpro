package com.manuel.vigicontrol.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String username;

    private String role;

    private boolean enabled;

}