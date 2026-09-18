package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.model.medical.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repo;

    public Page<Patient> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) return repo.findAll(pageable);
        String q = query.replace("-", "").toLowerCase();
        return repo.searchByDuiOrName(q, pageable);
    }

    @Transactional
    public Patient create(Patient p) {
        // Setea el tenant desde el JWT del usuario logueado (VG-08)
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        com.sv.grupo7.medisuite.model.tenant.Tenant t = new com.sv.grupo7.medisuite.model.tenant.Tenant();
        t.setId(tid);
        p.setTenant(t);
        return repo.save(p);
    }

    public Patient findById(Long id) {
        return repo.findById(id).orElseThrow(() ->
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