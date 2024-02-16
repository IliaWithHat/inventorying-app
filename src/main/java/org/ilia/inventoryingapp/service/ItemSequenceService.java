package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.ItemSequence;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.ItemSequenceRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemSequenceService {

    private final ItemSequenceRepository itemSequenceRepository;

    public void createSequence(User user) {
        itemSequenceRepository.save(new ItemSequence(null, 0L, user));
    }

    public Long nextval(UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        ItemSequence itemSequence = itemSequenceRepository.findItemSequenceByUserId(user.getId());
        itemSequence.setLastValue(itemSequence.getLastValue() + 1);
        return itemSequenceRepository.save(itemSequence).getLastValue();
    }

    public Long currval(UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return itemSequenceRepository.findItemSequenceByUserId(user.getId()).getLastValue();
    }
}
