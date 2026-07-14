package com.manuel.vigicontrol.user.controller;

import com.manuel.vigicontrol.user.dto.UserRequestDTO;
import com.manuel.vigicontrol.user.dto.UserResponseDTO;
import com.manuel.vigicontrol.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de usuarios
 *
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {

        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Crear nuevo usuarsio
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(201).body(userService.createUser(userRequestDTO));
    }

    /**
     * Encontrar el usuario con el id
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@Valid @PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));


    }

    /**
     * Actualizar un usuario con el id
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@Valid @PathVariable Long id,
                                                  @Valid @RequestBody UserRequestDTO user) {

        return ResponseEntity.ok(userService.updateUser(id, user));

    }

    /**
     * Borrar un usuario con el id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
