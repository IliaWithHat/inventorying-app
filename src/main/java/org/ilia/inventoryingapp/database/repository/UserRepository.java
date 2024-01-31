package org.ilia.inventoryingapp.database.repository;

import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findUsersByAdmin(User admin);

    Optional<User> findUserByIdAndAdmin(Integer id, User admin);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Optional<User> findUserByEmail(String email);
}
