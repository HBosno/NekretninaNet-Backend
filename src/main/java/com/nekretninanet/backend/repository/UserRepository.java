package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUserType(UserType userType);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
