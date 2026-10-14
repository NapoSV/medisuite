package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.MedicalRecordRepository;
import com.sv.grupo7.medisuite.dao.PrescriptionRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock PrescriptionRepository repo;
    @Mock MedicalRecordRepository recordRepo;
    @Mock DoctorRepository doctorRepo;
    @InjectMocks PrescriptionService service;

    private MedicalRecord expedienteValido(Long id) {
        MedicalRecord mr = mock(MedicalRecord.class, RETURNS_DEEP_STUBS);
        when(recordRepo.findById(id)).thenReturn(Optional.of(mr));
        return mr;
    }

    private Doctor doctorValido(Long userId) {
        Doctor d = mock(Doctor.class);
        when(doctorRepo.findByUserId(userId)).thenReturn(Optional.of(d));
        return d;
    }

    @Test
    void create_expedienteInexistente_lanzaBusinessException_yNoGuarda() {
        when(recordRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.create(99L, 1L, "dx", "notas", List.of()));

        verify(repo, never()).save(any());
    }

    @Test
    void create_usuarioNoEsDoctor_lanzaBusinessException_yNoGuarda() {
        expedienteValido(10L);
        when(doctorRepo.findByUserId(5L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "dx", "notas", new ArrayList<>(List.of(new PrescriptionItem()))));

        verify(repo, never()).save(any());
    }

    @Test
    void create_datosValidos_guardaRecetaConTenantDoctorExpedienteEItems() {
        MedicalRecord mr = expedienteValido(10L);
        Doctor doctor = doctorValido(5L);
        when(repo.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));
        PrescriptionItem item = new PrescriptionItem();

        Prescription result = service.create(10L, 5L, "Gripe", "Reposo", new ArrayList<>(List.of(item)));

        assertSame(mr.getPatient().getTenant(), result.getTenant());
        assertSame(mr, result.getMedicalRecord());
        assertSame(doctor, result.getDoctor());
        assertEquals("Gripe\nReposo", result.getInstructions());
        assertEquals(1, result.getItems().size());
        assertSame(result, item.getPrescription());
        verify(repo).save(result);
    }

    @Test
    void create_dosItems_conservanOrdenYApuntanALaMismaReceta() {
        expedienteValido(10L);
        doctorValido(5L);
        when(repo.save(any(Prescription.class))).thenAnswer(inv -> inv.getArgument(0));
        PrescriptionItem primero = new PrescriptionItem();
        PrescriptionItem segundo = new PrescriptionItem();

        Prescription result = service.create(10L, 5L, "dx", null,
                new ArrayList<>(List.of(primero, segundo)));

        assertSame(primero, result.getItems().get(0));
        assertSame(segundo, result.getItems().get(1));
        assertSame(result, primero.getPrescription());
        assertSame(result, segundo.getPrescription());
    }

    // Estos dos fallan HOY: son la brecha real del servicio (ver explicación)
    @Test
    void create_itemsNulos_lanzaBusinessException_yNoGuarda() {
        expedienteValido(10L);
        doctorValido(5L);

        assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "dx", "notas", null));

        verify(repo, never()).save(any());
    }

    @Test
    void create_itemsVacios_lanzaBusinessException_yNoGuarda() {
        expedienteValido(10L);
        doctorValido(5L);

        assertThrows(BusinessException.class,
                () -> service.create(10L, 5L, "dx", "notas", new ArrayList<>()));

        verify(repo, never()).save(any());
    }

    @Test
    void findById_existente_devuelveLaReceta() {
        Prescription p = new Prescription();
        when(repo.findById(7L)).thenReturn(Optional.of(p));

        assertSame(p, service.findById(7L));
    }

    @Test
    void findById_ausente_lanzaExcepcionConElId() {
        when(repo.findById(7L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(7L));

        assertTrue(ex.getMessage().contains("7"));
    }
}