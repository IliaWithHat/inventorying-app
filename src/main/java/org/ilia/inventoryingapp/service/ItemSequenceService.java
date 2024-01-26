package org.ilia.inventoryingapp.service;

import org.ilia.inventoryingapp.database.entity.ItemSequence;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.ItemSequenceRepository;
import org.ilia.inventoryingapp.dto.UserDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemSequenceService {

    private final ItemSequenceRepository itemSequenceRepository;
    private final UserService userService;

    public ItemSequenceService(ItemSequenceRepository itemSequenceRepository, @Lazy UserService userService) {
        this.itemSequenceRepository = itemSequenceRepository;
        this.userService = userService;
    }

    public void createSequence(User user) {
        ItemSequence itemSequence = ItemSequence.builder()
                .lastValue(0L)
                .user(user)
                .build();
        itemSequenceRepository.save(itemSequence);
    }

    public Long nextval(String email) {
        UserDto user = userService.findUserByEmail(email);
        ItemSequence itemSequence = itemSequenceRepository.findItemSequenceByUserId(user.getId());
        itemSequence.setLastValue(itemSequence.getLastValue() + 1);
        return itemSequenceRepository.save(itemSequence).getLastValue();
    }

    public Long currval(String email) {
        UserDto user = userService.findUserByEmail(email);
        return itemSequenceRepository.findItemSequenceByUserId(user.getId()).getLastValue();
    }
}
