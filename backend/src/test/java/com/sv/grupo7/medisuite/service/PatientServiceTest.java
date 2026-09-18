package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.model.medical.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Test
    void search_sinQuery_debeRetornarTodosLosPacientes() {
        Pageable pageable = PageRequest.of(0, 10);
        Patient patient = new Patient();
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(repo.findAll(pageable)).thenReturn(page);

        Page<Patient> result = patientService.search("", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repo).findAll(pageable);
    }

    @Test
    void search_conQuery_debeBuscarPorDuiONombre() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(new Patient()));

        when(repo.searchByDuiOrName("061234567", pageable)).thenReturn(page);

        Page<Patient> result =
                patientService.search("0612-34567", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repo).searchByDuiOrName("061234567", pageable);
    }

    @Test
    void findById_pacienteExistente_debeRetornarPaciente() {
        Patient patient = new Patient();

        when(repo.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.findById(1L);

        assertSame(patient, result);
    }

    @Test
    void findById_pacienteNoExiste_debeLanzarRuntimeException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.findById(99L)
        );

        assertEquals("Paciente 99 no existe", exception.getMessage());
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

        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        Patient result = patientService.update(1L, data);

        assertEquals("Nicole", result.getFirstName());
        assertEquals("Sanchez", result.getLastName());
        assertEquals("06123456-7", result.getDui());
        assertEquals("7777-7777", result.getPhone());
        assertEquals("Sonsonate", result.getAddress());
    }
}
