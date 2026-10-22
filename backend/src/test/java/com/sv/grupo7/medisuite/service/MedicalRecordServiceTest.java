package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.MedicalRecordRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.dao.PrescriptionRepository;
import com.sv.grupo7.medisuite.dao.VitalSignRepository;
import com.sv.grupo7.medisuite.dao.TenantRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.MedicalRecord;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.medical.VitalSign;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("regression")
@DisplayName("Pruebas unitarias del servicio de expedientes médicos")
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

    @Mock
    private TenantRepository tenantRepo;

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
            verify(patientRepo, never()).findByIdAndTenantId(anyLong(), anyLong());
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
            when(patientRepo.findByIdAndTenantId(10L, 1L))
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

            verify(patientRepo).findByIdAndTenantId(10L, 1L);
            verify(recordRepo).save(any(MedicalRecord.class));
        }
    }
@Test
    void findFullByPatientId_pacienteDeOtroTenant_debeLanzarBusinessException() {

        Long patientId = 50L;
        Long tenantActualId = 1L;

        try (MockedStatic<TenantContext> tenantContext =
                     mockStatic(TenantContext.class)) {

            tenantContext.when(TenantContext::currentTenantId)
                    .thenReturn(tenantActualId);

            when(recordRepo.findByPatientIdAndTenantId(
                    patientId, tenantActualId))
                    .thenReturn(Optional.empty());

            when(patientRepo.findByIdAndTenantId(
                    patientId, tenantActualId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> medicalRecordService.findFullByPatientId(patientId)
            );

            assertEquals("Paciente no existe", exception.getMessage());

            verify(recordRepo).findByPatientIdAndTenantId(
                    patientId, tenantActualId);

            verify(patientRepo).findByIdAndTenantId(
                    patientId, tenantActualId);

            verify(recordRepo, never()).save(any(MedicalRecord.class));
            verifyNoInteractions(prescriptionRepo, vitalSignRepo);
        }
    }
        @Test
    void addVitalSign_pacienteDeOtroTenant_debeLanzarBusinessException() {

        Long patientId = 50L;
        Long tenantActualId = 1L;

        Tenant tenantActual = new Tenant();
        tenantActual.setId(tenantActualId);

        MedicalRecordService.VitalSignRequest request =
                new MedicalRecordService.VitalSignRequest(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Dolor de cabeza",
                        "NORMAL"
                );

        try (MockedStatic<TenantContext> tenantContext =
                     mockStatic(TenantContext.class)) {

            tenantContext.when(TenantContext::currentTenantId)
                    .thenReturn(tenantActualId);

            when(tenantRepo.findById(tenantActualId))
                    .thenReturn(Optional.of(tenantActual));

            when(patientRepo.findByIdAndTenantId(
                    patientId, tenantActualId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> medicalRecordService.addVitalSign(
                            patientId, request)
            );

            assertEquals("Paciente no existe", exception.getMessage());

            verify(tenantRepo).findById(tenantActualId);
            verify(patientRepo).findByIdAndTenantId(
                    patientId, tenantActualId);

            verifyNoInteractions(recordRepo, vitalSignRepo);
        }
    }

    @Test
    @DisplayName("addVitalSign rechaza prioridades fuera de la escala NORMAL/URGENTE/EMERGENCIA")
    void addVitalSign_prioridadInvalida_debeLanzarBusinessException() {
        MedicalRecordService.VitalSignRequest request =
                new MedicalRecordService.VitalSignRequest(null, null, null, null, null, null, "CRITICAL");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> medicalRecordService.addVitalSign(1L, request)
        );

        assertEquals("Prioridad no valida: use NORMAL, URGENTE o EMERGENCIA", exception.getMessage());
        verifyNoInteractions(tenantRepo, patientRepo, recordRepo, vitalSignRepo);
    }

    @Test
    @DisplayName("addVitalSign normaliza mayusculas y guarda la prioridad en la escala vigente")
    void addVitalSign_prioridadEnMinusculas_debeGuardarseNormalizada() {
        Long tenantId = 1L;
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        Patient patient = new Patient();
        MedicalRecord record = new MedicalRecord();
        MedicalRecordService.VitalSignRequest request =
                new MedicalRecordService.VitalSignRequest(null, null, null, null, null, null, " emergencia ");

        try (MockedStatic<TenantContext> tenantContext = mockStatic(TenantContext.class)) {
            tenantContext.when(TenantContext::currentTenantId).thenReturn(tenantId);
            when(tenantRepo.findById(tenantId)).thenReturn(Optional.of(tenant));
            when(patientRepo.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(patient));
            when(recordRepo.findByPatientIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(record));
            when(vitalSignRepo.save(any(VitalSign.class))).thenAnswer(inv -> inv.getArgument(0));

            VitalSign saved = medicalRecordService.addVitalSign(1L, request);

            assertEquals("EMERGENCIA", saved.getPriority());
        }
    }
}
