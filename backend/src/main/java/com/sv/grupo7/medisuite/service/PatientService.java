package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repo;

    @Transactional(readOnly = true)
    public Page<Patient> search(String query, Pageable pageable) {
        Long tid = TenantContext.currentTenantId();
        if (query == null || query.isBlank()) return repo.findByTenantId(tid, pageable);
        String q = query.replace("-", "").toLowerCase();
        return repo.searchByDuiOrName(tid, q, pageable);
    }

    @Transactional
    public Patient create(Patient p) {
        Long tid = TenantContext.currentTenantId();
        com.sv.grupo7.medisuite.model.tenant.Tenant t = new com.sv.grupo7.medisuite.model.tenant.Tenant();
        t.setId(tid);
        p.setTenant(t);
        return repo.save(p);
    }

    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return repo.findByIdAndTenantId(id, TenantContext.currentTenantId()).orElseThrow(() ->
            new RuntimeException("Paciente " + id + " no existe"));
    }

    @Transactional
    public Patient update(Long id, Patient data) {
        Patient p = findById(id);
        p.setFirstName(data.getFirstName());
        p.setLastName(data.getLastName());
        p.setDui(data.getDui());
        p.setPhone(data.getPhone());
        p.setAddress(data.getAddress());
        return p;
    }
}