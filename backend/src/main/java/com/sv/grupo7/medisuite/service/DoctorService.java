package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.SpecialtyRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dto.doctor.CreateDoctorRequest;
import com.sv.grupo7.medisuite.dto.doctor.UpdateDoctorRequest;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.medical.Specialty;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.model.users.User;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    private final SpecialtyRepository specialtyRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Doctor> findAll() {
        return doctorRepo.findByTenantId(TenantContext.currentTenantId());
    }

    @Transactional(readOnly = true)
    public Doctor findById(Long id) {
        return doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
    }

    @Transactional
    public Doctor create(CreateDoctorRequest req) {
        Long tenantId = TenantContext.currentTenantId();

        Specialty specialty = specialtyRepo.findById(req.specialtyId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        Tenant tenant = new Tenant();
        tenant.setId(tenantId);

        User user = new User();
        user.setTenant(tenant);
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setEmail(req.email());
        user.setCif(req.cif());
        user.setPasswordHash(passwordEncoder.encode("Demo2026!"));
        user.setRole("DOCTOR");
        user.setActive(true);
        user.setFailedLoginAttempts(0);
        user.setMustChangePassword(true);
        User savedUser = userRepo.save(user);

        Doctor doctor = new Doctor();
        doctor.setTenant(tenant);
        doctor.setUser(savedUser);
        doctor.setSpecialty(specialty);
        doctor.setLicenseNumber(req.licenseNumber());
        return doctorRepo.save(doctor);
    }

    @Transactional
    public Doctor update(Long id, UpdateDoctorRequest req) {
        Doctor doctor = findById(id);
        if (req.specialtyId() != null) {
            Specialty specialty = specialtyRepo.findById(req.specialtyId())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
            doctor.setSpecialty(specialty);
        }
        if (req.licenseNumber() != null && !req.licenseNumber().isBlank()) {
            doctor.setLicenseNumber(req.licenseNumber());
        }
        return doctorRepo.save(doctor);
    }
}
