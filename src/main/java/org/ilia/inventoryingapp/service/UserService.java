package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(userMapper::toUserDto)
                .toList();
    }

    public Optional<UserDto> findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto);
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Transactional
    public Optional<UserDto> update(UserDto userDto, Integer id) {
        return userRepository.findById(id)
                .map(user -> userMapper.copyUserDtoToUser(userDto, user))
                .map(userRepository::saveAndFlush)
                .map(userMapper::toUserDto);
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            userRepository.flush();
            return true;
        }
        return false;
    }
}
