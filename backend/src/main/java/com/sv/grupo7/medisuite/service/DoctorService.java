package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository repo;

    @Transactional(readOnly = true)
    public List<Doctor> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Doctor findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
    }

    @Transactional
    public Doctor create(Doctor d) {
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        com.sv.grupo7.medisuite.model.tenant.Tenant t = new com.sv.grupo7.medisuite.model.tenant.Tenant();
        t.setId(tid);
        d.setTenant(t);
        return repo.save(d);
    }
}