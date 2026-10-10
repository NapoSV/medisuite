package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.security.TenantContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repo;

    @InjectMocks
    private PatientService patientService;

    private static final Long TENANT_ID = 1L;

    @Test
    void search_sinQuery_debeRetornarPacientesDelTenant() {
        Pageable pageable = PageRequest.of(0, 10);
        Patient patient = new Patient();
        Page<Patient> page = new PageImpl<>(List.of(patient));

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
            when(repo.findByTenantId(TENANT_ID, pageable)).thenReturn(page);

            Page<Patient> result = patientService.search("", pageable);

            assertEquals(1, result.getTotalElements());
            verify(repo).findByTenantId(TENANT_ID, pageable);
        }
    }

    @Test
    void search_conQuery_debeBuscarPorDuiONombreEnTenant() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(new Patient()));

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
            when(repo.searchByDuiOrName(TENANT_ID, "061234567", pageable)).thenReturn(page);

            Page<Patient> result = patientService.search("0612-34567", pageable);

            assertEquals(1, result.getTotalElements());
            verify(repo).searchByDuiOrName(TENANT_ID, "061234567", pageable);
        }
    }

    @Test
    void findById_pacienteExistente_debeRetornarPaciente() {
        Patient patient = new Patient();

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
            when(repo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(patient));

            Patient result = patientService.findById(1L);

            assertSame(patient, result);
        }
    }

    @Test
    void findById_pacienteNoExiste_debeLanzarRuntimeException() {
        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
            when(repo.findByIdAndTenantId(99L, TENANT_ID)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> patientService.findById(99L)
            );

            assertEquals("Paciente 99 no existe", exception.getMessage());
        }
    }

    @Test
    void update_debeActualizarDatosDelPaciente() {
        Patient existing = new Patient();
        Patient data = new Patient();

        data.setFirstName("Nicole");
        data.setLastName("Sanchez");
        data.setDui("06123456-7");
        data.setPhone("7777-7777");
        data.setAddress("Sonsonate");

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
            when(repo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(existing));

            Patient result = patientService.update(1L, data);

            assertEquals("Nicole", result.getFirstName());
            assertEquals("Sanchez", result.getLastName());
            assertEquals("06123456-7", result.getDui());
            assertEquals("7777-7777", result.getPhone());
            assertEquals("Sonsonate", result.getAddress());
        }
    }
}
