package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
}
