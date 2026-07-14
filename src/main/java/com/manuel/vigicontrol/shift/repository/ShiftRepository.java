package com.manuel.vigicontrol.shift.repository;

import com.manuel.vigicontrol.shift.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<ShiftEntity, Long >{

        };
