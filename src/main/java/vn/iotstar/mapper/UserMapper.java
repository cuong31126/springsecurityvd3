package vn.iotstar.mapper;

import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.User;

public interface UserMapper {
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
}
