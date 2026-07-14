package com.manuel.vigicontrol.user.service;

import com.manuel.vigicontrol.exception.BadRequestException;
import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.user.dto.UserRequestDTO;
import com.manuel.vigicontrol.user.dto.UserResponseDTO;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.enums.RoleName;
import com.manuel.vigicontrol.user.mapper.UserMapper;
import com.manuel.vigicontrol.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de UserService.
 * Las dependencias (UserRepository, PasswordEncoder, UserMapper) se simulan
 * con Mockito para probar la lógica del Service de forma aislada,
 * sin necesidad de levantar Spring ni una base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity user;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("manuel");
        user.setPassword("hashedPassword");
        user.setRole(RoleName.ROLE_VIGILANTE);
        user.setEnabled(true);

        requestDTO = new UserRequestDTO("manuel", "manolo123", "ROLE_ADMIN");
        responseDTO = new UserResponseDTO(1L, "manuel", "ROLE_VIGILANTE", true);
    }

    // ---------- createUser ----------

    @Test
    void createUser_deberiaCrearUsuarioCorrectamente_cuandoUsernameNoExiste() {
        when(userRepository.findByUsername("manuel")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("manolo123")).thenReturn("hashedPassword");
        when(userMapper.toUserResponseDTO(any(UserEntity.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("manuel");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder, times(1)).encode("manolo123");
    }

    @Test
    void createUser_deberiaLanzarBadRequestException_cuandoUsernameYaExiste() {
        when(userRepository.findByUsername("manuel")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El usuario ya existe");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // ---------- getUserById ----------

    @Test
    void getUserById_deberiaDevolverUsuario_cuandoExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("manuel");
    }

    @Test
    void getUserById_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    // ---------- getAllUsers ----------

    @Test
    void getAllUsers_deberiaDevolverListaDeUsuarios() {
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setUsername("lola");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.toUserResponseDTO(any(UserEntity.class))).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        verify(userMapper, times(2)).toUserResponseDTO(any(UserEntity.class));
    }

    @Test
    void getAllUsers_deberiaDevolverListaVacia_cuandoNoHayUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    // ---------- updateUser ----------

    @Test
    void updateUser_deberiaActualizarDatos_cuandoUsuarioExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newHashedPassword");
        when(userMapper.toUserResponseDTO(any(UserEntity.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(1L, requestDTO);

        assertThat(result).isNotNull();
        assertThat(user.getRole()).isEqualTo(RoleName.ROLE_ADMIN); // viene de requestDTO
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_deberiaLanzarNotFoundException_cuandoUsuarioNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, requestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // ---------- deleteUser ----------

    @Test
    void deleteUser_deberiaEliminarUsuario_cuandoExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_deberiaLanzarNotFoundException_cuandoNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository, never()).delete(any(UserEntity.class));
    }
}
