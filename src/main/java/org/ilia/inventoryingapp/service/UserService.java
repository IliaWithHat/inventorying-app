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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemSequenceService itemSequenceService;
    private final SessionService sessionService;

    public List<UserDto> findAll(UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        return userRepository.findUsersByAdmin(admin, Sort.by("id")).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public Optional<UserDto> findById(Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        return (admin.getId().equals(id) ? Optional.of(admin) : userRepository.findUserByIdAndAdmin(id, admin))
                .map(userMapper::toUserDto);
    }

    @Transactional
    public UserDto create(UserDetails userDetails, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        userRepository.save(user);

        if (userDetails == null) {
            itemSequenceService.createSequence(user);
        } else {
            User admin = ((UserDetailsImpl) userDetails).getUser();
            long usersCount = userRepository.countByAdmin(admin);
            if (usersCount < 10) {
                user.setAdmin(admin);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        return userMapper.toUserDto(user);
    }

    @Transactional
    public Optional<UserDto> update(UserDto userDto, Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        return (admin.getId().equals(id) ? Optional.of(admin) : userRepository.findUserByIdAndAdmin(id, admin))
                .map(u -> userMapper.copyUserDtoToUser(userDto, u))
                .map(userRepository::saveAndFlush)
                .map(userMapper::toUserDto);
    }

    @Transactional
    public Optional<UserDto> delete(Integer id, UserDetails userDetails) {
        User admin = ((UserDetailsImpl) userDetails).getUser();
        return (id.equals(admin.getId()) ? Optional.of(admin) : userRepository.findUserByIdAndAdmin(id, admin))
                .map(user -> {
                    sessionService.expireSession(user);
                    if (id.equals(admin.getId()))
                        sessionService.expireSession(userRepository.findUsersByAdmin(admin));

                    userRepository.delete(user);
                    userRepository.flush();
                    return userMapper.toUserDto(user);
                });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user:" + email));
    }
}
