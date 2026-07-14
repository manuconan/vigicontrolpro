package com.manuel.vigicontrol.user.repository;

import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findByRole(RoleName role);
}
