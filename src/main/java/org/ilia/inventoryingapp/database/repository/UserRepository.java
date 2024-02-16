package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findUsersByAdmin(User admin, Sort sort);

    long countByAdmin(User admin);

    Optional<User> findUserByIdAndAdmin(Integer id, User admin);

    Optional<User> findUserByEmail(String email);
}
