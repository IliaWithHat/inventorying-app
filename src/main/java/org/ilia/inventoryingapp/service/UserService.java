package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.mapper.UserMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemSequenceService itemSequenceService;

    public List<UserDto> findAll(UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        return userRepository.findUsersByAdmin(admin, Sort.by("id")).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public Optional<UserDto> findById(Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        Optional<User> user;
        if (admin.getId().equals(id)) {
            user = Optional.of(admin);
        } else {
            user = userRepository.findUserByIdAndAdmin(id, admin);
        }
        return user.map(userMapper::toUserDto);
    }

    @Transactional
    public UserDto create(UserDetails userDetails, UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (userDetails != null) {
            User admin = ((UserDetailsImpl) userDetails).getUser();
            long usersCount = userRepository.countByAdmin(admin);
            if (usersCount < 10) {
                user.setAdmin(admin);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        User savedUser = userRepository.save(user);

        if (userDetails == null) {
            itemSequenceService.createSequence(savedUser);
        }

        return userMapper.toUserDto(savedUser);
    }

    @Transactional
    public Optional<UserDto> update(UserDto userDto, Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        Optional<User> user;
        if (admin.getId().equals(id)) {
            user = Optional.of(admin);
        } else {
            user = userRepository.findUserByIdAndAdmin(id, admin);
        }
        return user
                .map(u -> userMapper.copyUserDtoToUser(userDto, u))
                .map(userRepository::saveAndFlush)
                .map(userMapper::toUserDto);
    }

    @Transactional
    public Optional<UserDto> delete(Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        boolean isAdmin = id.equals(admin.getId());
        Optional<User> user = isAdmin ? Optional.of(admin) : userRepository.findUserByIdAndAdmin(id, admin);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            userRepository.flush();
            return user.map(userMapper::toUserDto);
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .map(user -> new UserDetailsImpl(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singleton(user.getRole()),
                        user
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user:" + email));
    }
}
