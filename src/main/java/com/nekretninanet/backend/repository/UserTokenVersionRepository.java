package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.UserTokenVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenVersionRepository extends JpaRepository<UserTokenVersion, Long> {

}
