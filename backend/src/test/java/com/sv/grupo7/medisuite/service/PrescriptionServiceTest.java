package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    private static final Long TENANT_A = 1L;
    private static final Long TENANT_B = 2L;

    @Mock private PrescriptionRepository repo;
    @Mock private MedicalRecordRepository recordRepo;
    @Mock private DoctorRepository doctorRepo;
    @InjectMocks private PrescriptionService service;

    private MockedStatic<TenantContext> tenantCtx;

    @BeforeEach
    void setUp() {
        tenantCtx = Mockito.mockStatic(TenantContext.class,
                withSettings().strictness(Strictness.LENIENT));
        tenantCtx.when(TenantContext::currentTenantId).thenReturn(TENANT_A);
    }

    @AfterEach
    void tearDown() {
        tenantCtx.close();
    }

    private List<PrescriptionItem> twoItems() {
        List<PrescriptionItem> items = new ArrayList<>();
        items.add(new PrescriptionItem());
        items.add(new PrescriptionItem());
        return items;
    }

    // 1 y 8. Receta válida: guarda con expediente, doctor, ítems enlazados y en orden
    @Test
    void create_validData_savesPrescriptionWithLinkedItemsInOrder() {
        MedicalRecord mr = mock(MedicalRecord.class, RETURNS_DEEP_STUBS);
        Doctor doctor = mock(Doctor.class);
        when(doctor.getTenantId()).thenReturn(TENANT_A);
        when(recordRepo.findByIdAndTenantId(10L, TENANT_A)).thenReturn(Optional.of(mr));
        when(doctorRepo.findByUserId(5L)).thenReturn(Optional.of(doctor));
        when(repo.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));

        List<PrescriptionItem> items = twoItems();
        PrescriptionItem first = items.get(0);
        PrescriptionItem second = items.get(1);

        Prescription result = service.create(10L, 5L, "Gripe", "Reposo", items);

        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
        verify(repo).save(captor.capture());
        Prescription saved = captor.getValue();

        assertSame(saved, result);
        assertSame(mr, saved.getMedicalRecord());
        assertSame(doctor, saved.getDoctor());
        assertEquals("Gripe\nReposo", saved.getInstructions());
        assertEquals(2, saved.getItems().size());
        assertSame(first, saved.getItems().get(0));
        assertSame(second, saved.getItems().get(1));
        assertSame(saved, first.getPrescription());
        assertSame(saved, second.getPrescription());
    }

    // 4. Lista vacía
    @Test
    void create_emptyItems_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "Gripe", null, new ArrayList<>()));
        assertEquals("La receta requiere al menos un medicamento", ex.getMessage());
        verify(repo, never()).save(any());
        verifyNoInteractions(recordRepo, doctorRepo);
    }

    // 5. Lista nula: error controlado, no NullPointerException
    @Test
    void create_nullItems_throwsBusinessExceptionNotNpe() {
        assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "Gripe", null, null));
        verify(repo, never()).save(any());
    }

    // 2. Expediente inexistente
    @Test
    void create_recordNotFound_throwsAndDoesNotSave() {
        when(recordRepo.findByIdAndTenantId(99L, TENANT_A)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(99L, 5L, "Gripe", null, twoItems()));
        assertEquals("Expediente no existe", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // 3. userId que no es doctor
    @Test
    void create_userIsNotDoctor_throwsAndDoesNotSave() {
        MedicalRecord mr = mock(MedicalRecord.class);
        when(recordRepo.findByIdAndTenantId(10L, TENANT_A)).thenReturn(Optional.of(mr));
        when(doctorRepo.findByUserId(77L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(10L, 77L, "Gripe", null, twoItems()));
        assertEquals("El usuario autenticado no es un doctor", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // Doctor de otro tenant
    @Test
    void create_doctorFromOtherTenant_throwsAndDoesNotSave() {
        MedicalRecord mr = mock(MedicalRecord.class);
        Doctor doctor = mock(Doctor.class);
        when(doctor.getTenantId()).thenReturn(TENANT_B);
        when(recordRepo.findByIdAndTenantId(10L, TENANT_A)).thenReturn(Optional.of(mr));
        when(doctorRepo.findByUserId(5L)).thenReturn(Optional.of(doctor));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "Gripe", null, twoItems()));
        assertEquals("Doctor no pertenece al tenant actual", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // 9. Tenant A no puede crear receta sobre expediente del tenant B
    @Test
    void create_recordOfOtherTenant_isRejected() {
        // el expediente 50 existe pero pertenece al tenant B: la consulta filtrada por A no lo devuelve
        when(recordRepo.findByIdAndTenantId(50L, TENANT_A)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.create(50L, 5L, "Gripe", null, twoItems()));
        verify(recordRepo).findByIdAndTenantId(50L, TENANT_A);
        verify(recordRepo, never()).findById(any());
        verify(repo, never()).save(any());
    }

    // 7. findById existente
    @Test
    void findById_existing_returnsPrescription() {
        Prescription p = new Prescription();
        when(repo.findByIdAndTenantId(7L, TENANT_A)).thenReturn(Optional.of(p));

        assertSame(p, service.findById(7L));
    }

    // 7. findById ausente
    @Test
    void findById_missing_throwsBusinessException() {
        when(repo.findByIdAndTenantId(404L, TENANT_A)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.findById(404L));
        assertEquals("Receta 404 no existe", ex.getMessage());
    }

    // 10. Tenant A no puede leer receta del tenant B
    @Test
    void findById_prescriptionOfOtherTenant_isRejected() {
        when(repo.findByIdAndTenantId(8L, TENANT_A)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.findById(8L));
        verify(repo).findByIdAndTenantId(8L, TENANT_A);
        verify(repo, never()).findById(any());
    }
}