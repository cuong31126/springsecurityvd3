package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.UserMapper;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> users = (keyword == null || keyword.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.search(keyword.trim(), pageable);

        return users.map(u -> {
            UserDTO dto = mapper.toDTO(u);
            dto.setProductCount(userRepository.countProductsByUserId(u.getId()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại với ID: " + id));
        UserDTO dto = mapper.toDTO(user);
        dto.setProductCount(userRepository.countProductsByUserId(user.getId()));
        return dto;
    }

    @Override
    @Transactional
    public UserDTO create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Role role = roleRepository.findByName(dto.getRoleName() != null ? dto.getRoleName() : "ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role không tồn tại"));

        User user = mapper.toEntity(dto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEnabled(dto.isEnabled());
        return mapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại với ID: " + id));

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setEnabled(dto.isEnabled());

        if (dto.getRoleName() != null && !dto.getRoleName().isBlank()) {
            Role role = roleRepository.findByName(dto.getRoleName())
                    .orElse(user.getRole());
            user.setRole(role);
        }

        return mapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại với ID: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProducts(Long userId) {
        return userRepository.countProductsByUserId(userId);
    }
}
