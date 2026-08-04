package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByUserId(Long userId);
}
