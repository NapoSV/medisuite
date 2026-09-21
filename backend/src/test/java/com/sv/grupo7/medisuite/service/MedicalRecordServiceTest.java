package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.MedicalRecordRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.dao.PrescriptionRepository;
import com.sv.grupo7.medisuite.dao.VitalSignRepository;
import com.sv.grupo7.medisuite.model.medical.MedicalRecord;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.medical.VitalSign;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository recordRepo;

    @Mock
    private PatientRepository patientRepo;

    @Mock
    private PrescriptionRepository prescriptionRepo;

    @Mock
    private VitalSignRepository vitalSignRepo;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Test
    void findFullByPatientId_expedienteExistente_debeRetornarDatosCompletos() {
        Patient patient = new Patient();

        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        record.setPatient(patient);
        record.setGeneralNotes("Paciente estable");

        List<Prescription> prescriptions = List.of(new Prescription());
        List<VitalSign> vitalSigns = List.of(new VitalSign());

        try (MockedStatic<TenantContext> tenantContext = mockStatic(TenantContext.class)) {
            tenantContext.when(TenantContext::currentTenantId).thenReturn(1L);

            when(recordRepo.findByPatientIdAndTenantId(10L, 1L))
                    .thenReturn(Optional.of(record));
            when(prescriptionRepo.findByMedicalRecordIdOrderByIssuedOnDesc(1L))
                    .thenReturn(prescriptions);
            when(vitalSignRepo.findByMedicalRecordIdOrderByRecordedAtDesc(1L))
                    .thenReturn(vitalSigns);

            Map<String, Object> result =
                    medicalRecordService.findFullByPatientId(10L);

            assertEquals(1L, result.get("id"));
            assertSame(patient, result.get("patient"));
            assertEquals("Paciente estable", result.get("generalNotes"));
            assertSame(prescriptions, result.get("prescriptions"));
            assertSame(vitalSigns, result.get("vitalSigns"));

            verify(recordRepo).findByPatientIdAndTenantId(10L, 1L);
            verify(patientRepo, never()).findById(anyLong());
        }
    }

    @Test
    void findFullByPatientId_sinExpediente_debeCrearUnoNuevo() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);

        Patient patient = new Patient();
        patient.setTenant(tenant);

        MedicalRecord savedRecord = new MedicalRecord();
        savedRecord.setId(5L);
        savedRecord.setPatient(patient);
        savedRecord.setTenant(tenant);

        try (MockedStatic<TenantContext> tenantContext = mockStatic(TenantContext.class)) {
            tenantContext.when(TenantContext::currentTenantId).thenReturn(1L);

            when(recordRepo.findByPatientIdAndTenantId(10L, 1L))
                    .thenReturn(Optional.empty());
            when(patientRepo.findById(10L))
                    .thenReturn(Optional.of(patient));
            when(recordRepo.save(any(MedicalRecord.class)))
                    .thenReturn(savedRecord);
            when(prescriptionRepo.findByMedicalRecordIdOrderByIssuedOnDesc(5L))
                    .thenReturn(List.of());
            when(vitalSignRepo.findByMedicalRecordIdOrderByRecordedAtDesc(5L))
                    .thenReturn(List.of());

            Map<String, Object> result =
                    medicalRecordService.findFullByPatientId(10L);

            assertEquals(5L, result.get("id"));
            assertSame(patient, result.get("patient"));
            assertEquals("", result.get("generalNotes"));

            verify(patientRepo).findById(10L);
            verify(recordRepo).save(any(MedicalRecord.class));
        }
    }
}