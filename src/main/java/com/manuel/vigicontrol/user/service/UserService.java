package com.manuel.vigicontrol.user.service;

import com.manuel.vigicontrol.exception.BadRequestException;
import com.manuel.vigicontrol.exception.NotFoundException;
import com.manuel.vigicontrol.user.dto.UserRequestDTO;
import com.manuel.vigicontrol.user.dto.UserResponseDTO;
import com.manuel.vigicontrol.user.entity.UserEntity;
import com.manuel.vigicontrol.user.enums.RoleName;
import com.manuel.vigicontrol.user.mapper.UserMapper;
import com.manuel.vigicontrol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    /**
     * Metodo para crear un nuevo usuario, asignarle un rol y guardar en la base de datos.
     *
     */

    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("El usuario ya existe");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRole(RoleName.ROLE_VIGILANTE); // rol por defecto
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    /*
     * Metodo para buscar un usuario por id, si no se encuentra lanza una excepcion.
     *
     * @param id Identificador del usuario a buscar.
     * @return UserResponseDTO con los datos del usuario encontrado.
     * @throws NotFoundException Si el usuario no existe.
     **/
    public UserResponseDTO getUserById(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return userMapper.toUserResponseDTO(user);
    }

    /**
     * Método para listar todos los usuarios registrados en la base de datos.
     *
     * @return List<UserResponseDTO> Lista de usuarios con sus datos.
     * @throws NotFoundException Si no hay usuarios registrados.
     */

    public List<UserResponseDTO> getAllUsers() {

        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toUserResponseDTO)
                .toList();
    }

    /**
     * Método paginado para listar usuarios. Útil cuando la tabla crece
     * y no conviene devolver todos los registros de golpe.
     *
     * @param pageable Información de página (número, tamaño, orden).
     * @return Page<UserResponseDTO> página de usuarios.
     */
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDTO);
    }

    /**
     * Método updateUser para actualizar los datos de un usuario existente.
     *
     * @param id      Identificador del usuario a actualizar.
     * @param request UserRequestDTO con los nuevos datos del usuario.
     * @return UserResponseDTO con los datos actualizados del usuario.
     * @throws NotFoundException Si el usuario no existe.
     *
     */

    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setUsername(request.getUsername());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoleName() != null) {
            user.setRole(RoleName.valueOf(request.getRoleName()));
        }

        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);

    }

    /**
     * Método deleteUser para eliminar un usuario existente.
     *
     * @param id Identificador del usuario a eliminar.
     * @throws NotFoundException Si el usuario no existe.
     *
     */
    public void deleteUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        userRepository.delete(user);
    }
}


