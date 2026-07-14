package com.manuel.vigicontrol.user.mapper;

import com.manuel.vigicontrol.user.dto.UserRequestDTO;
import com.manuel.vigicontrol.user.dto.UserResponseDTO;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.enums.RoleName;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toUserResponseDTO(UserEntity user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.isEnabled()
        );
    }

    public UserEntity toEntity(UserRequestDTO request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setRole(RoleName.valueOf(request.getRoleName()));
        return user;
    }
}