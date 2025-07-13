package com.learning.security.repos;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.security.enums.ERole;
import com.learning.security.models.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole roleName);

}
