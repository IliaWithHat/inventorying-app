package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);
}
