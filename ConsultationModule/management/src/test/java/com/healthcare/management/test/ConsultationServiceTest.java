package com.healthcare.management.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.healthcare.management.dao.AppointmentDAO;
import com.healthcare.management.dao.ConsultationDAO;
import com.healthcare.management.dto.ConsultationDto;
import com.healthcare.management.entity.Consultation;
import com.healthcare.management.exception.ConsultationAlreadyExistsException;
import com.healthcare.management.exception.NoConsultationDetailsFoundException;
import com.healthcare.management.service.ConsultationService;

/**
 * ConsultationServiceTest is a test class for the ConsultationService.
 * It uses Mockito to mock dependencies and JUnit 5 for testing.
 *
 * @ExtendWith(MockitoExtension.class) - Integrates Mockito with JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {

    @Mock
    private ConsultationDAO consultationDAO;

    @Mock
    private AppointmentDAO appointmentDAO;

    @InjectMocks
    private ConsultationService consultationService;

    private Consultation consultation1;
    private Consultation consultation2;
    private ConsultationDto consultationDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        consultation1 = new Consultation();
        consultation1.setConsultationId(UUID.randomUUID().toString());
        consultation1.setAppointmentId("appt1");
        consultation1.setNotes("Initial notes");
        consultation1.setPrescription("Medication A");

        consultation2 = new Consultation();
        consultation2.setConsultationId(UUID.randomUUID().toString());
        consultation2.setAppointmentId("appt2");
        consultation2.setNotes("Follow-up notes");
        consultation2.setPrescription("Medication B");

        consultationDto = new ConsultationDto();
        consultationDto.setAppointmentId("appt3");
        consultationDto.setNotes("New consultation notes");
        consultationDto.setPrescription("Medication C");
    }

    /**
     * Tests the getAllConsultationDetails method.
     * Verifies that the method returns the correct number of consultation records.
     */
    @Test
    public void testGetAllConsultationDetails() {
        List<Consultation> consultations = Arrays.asList(consultation1, consultation2);
        when(consultationDAO.findAll()).thenReturn(consultations);
        List<Consultation> result = consultationService.getAllConsultationDetails();
        assertEquals(2, result.size());
        verify(consultationDAO, times(1)).findAll();
    }

    /**
     * Tests the getConsultationDetailsById method.
     * Verifies that the method returns the correct consultation record.
     */
    @Test
    public void testGetConsultationDetailsById() {
        when(consultationDAO.findByConsultationId(consultation1.getConsultationId())).thenReturn(Optional.of(consultation1));
        Consultation result = consultationService.getConsultationDetailsById(consultation1.getConsultationId());
        assertNotNull(result);
        assertEquals(consultation1.getConsultationId(), result.getConsultationId());
        verify(consultationDAO, times(1)).findByConsultationId(consultation1.getConsultationId());
    }

    /**
     * Tests the getConsultationDetailsById method when no consultation is found.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testGetConsultationDetailsById_NotFound() {
        String nonExistingId = UUID.randomUUID().toString();
        when(consultationDAO.findByConsultationId(nonExistingId)).thenReturn(Optional.empty());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.getConsultationDetailsById(nonExistingId);
        });
        verify(consultationDAO, times(1)).findByConsultationId(nonExistingId);
    }

    /**
     * Tests the createConsultation method.
     * Verifies that the method creates a new consultation record and the UUID is set.
     */
    @Test
    public void testCreateConsultation() {
        when(consultationDAO.existsByAppointmentId(consultationDto.getAppointmentId())).thenReturn(false);
        Consultation savedConsultation = new Consultation();
        savedConsultation.setAppointmentId(consultationDto.getAppointmentId());
        savedConsultation.setNotes(consultationDto.getNotes());
        savedConsultation.setPrescription(consultationDto.getPrescription());
        savedConsultation.setConsultationId(UUID.randomUUID().toString()); // Simulate UUID generation on save
        when(consultationDAO.save(any(Consultation.class))).thenReturn(savedConsultation);

        Consultation result = consultationService.createConsultation(consultationDto);

        assertNotNull(result);
        assertEquals(consultationDto.getAppointmentId(), result.getAppointmentId());
        assertEquals(consultationDto.getNotes(), result.getNotes());
        assertEquals(consultationDto.getPrescription(), result.getPrescription());
        assertNotNull(result.getConsultationId());
        verify(consultationDAO, times(1)).existsByAppointmentId(consultationDto.getAppointmentId());
        verify(consultationDAO, times(1)).save(any(Consultation.class));
    }

    /**
     * Tests the createConsultation method when a consultation already exists.
     * Verifies that the method throws ConsultationAlreadyExistsException.
     */
    @Test
    public void testCreateConsultation_AlreadyExists() {
        when(consultationDAO.existsByAppointmentId(consultationDto.getAppointmentId())).thenReturn(true);
        assertThrows(ConsultationAlreadyExistsException.class, () -> {
            consultationService.createConsultation(consultationDto);
        });
        verify(consultationDAO, times(1)).existsByAppointmentId(consultationDto.getAppointmentId());
        verify(consultationDAO, never()).save(any(Consultation.class));
    }

    /**
     * Tests the findConDetailsByAppId method.
     * Verifies that the method returns the correct list of consultation records for a given appointment ID.
     */
    @Test
    public void testFindConDetailsByAppId() {
        List<Consultation> consultations = Arrays.asList(consultation1);
        when(consultationDAO.findConsultationDetailsByAppointmentId(consultation1.getAppointmentId())).thenReturn(consultations);
        List<Consultation> result = consultationService.findConDetailsByAppId(consultation1.getAppointmentId());
        assertEquals(1, result.size());
        assertEquals(consultation1.getConsultationId(), result.get(0).getConsultationId());
        verify(consultationDAO, times(1)).findConsultationDetailsByAppointmentId(consultation1.getAppointmentId());
    }

    /**
     * Tests the findConDetailsByAppId method when no consultation details are found for the given appointment ID.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testFindConDetailsByAppId_NotFound() {
        String nonExistingAppId = "nonExistingAppId";
        when(consultationDAO.findConsultationDetailsByAppointmentId(nonExistingAppId)).thenReturn(List.of());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.findConDetailsByAppId(nonExistingAppId);
        });
        verify(consultationDAO, times(1)).findConsultationDetailsByAppointmentId(nonExistingAppId);
    }

    /**
     * Tests the updateConsultationDetailsById method.
     * Verifies that the method updates the consultation record.
     */
    @Test
    public void testUpdateConsultationDetailsById() {
        ConsultationDto updatedDto = new ConsultationDto();
        updatedDto.setNotes("Updated Notes");
        updatedDto.setPrescription("Updated Prescription");

        when(consultationDAO.findById(consultation1.getConsultationId())).thenReturn(Optional.of(consultation1));
        when(consultationDAO.save(any(Consultation.class))).thenReturn(consultation1);

        Consultation result = consultationService.updateConsultationDetailsById(consultation1.getConsultationId(), updatedDto);

        assertNotNull(result);
        assertEquals(updatedDto.getNotes(), result.getNotes());
        assertEquals(updatedDto.getPrescription(), result.getPrescription());
        assertEquals(consultation1.getConsultationId(), result.getConsultationId());
        verify(consultationDAO, times(1)).findById(consultation1.getConsultationId());
        verify(consultationDAO, times(1)).save(consultation1);
    }

    /**
     * Tests the updateConsultationDetailsById method when no consultation is found.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testUpdateConsultationDetailsById_NotFound() {
        ConsultationDto updatedDto = new ConsultationDto();
        updatedDto.setNotes("Updated Notes");
        updatedDto.setPrescription("Updated Prescription");
        String nonExistingId = UUID.randomUUID().toString();
        when(consultationDAO.findById(nonExistingId)).thenReturn(Optional.empty());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.updateConsultationDetailsById(nonExistingId, updatedDto);
        });
        verify(consultationDAO, times(1)).findById(nonExistingId);
        verify(consultationDAO, never()).save(any(Consultation.class));
    }

    /**
     * Tests the updateConsultationByAppointmentId method.
     * Verifies that the method updates the consultation record by appointment ID.
     */
    @Test
    public void testUpdateConsultationByAppointmentId() {
        ConsultationDto updatedDto = new ConsultationDto();
        updatedDto.setNotes("Updated Notes");
        updatedDto.setPrescription("Updated Prescription");

        when(consultationDAO.findConsultationByAppointmentId(consultation1.getAppointmentId())).thenReturn(Optional.of(consultation1));
        when(consultationDAO.save(any(Consultation.class))).thenReturn(consultation1);

        Consultation result = consultationService.updateConsultationByAppointmentId(consultation1.getAppointmentId(), updatedDto);

        assertNotNull(result);
        assertEquals(updatedDto.getNotes(), result.getNotes());
        assertEquals(updatedDto.getPrescription(), result.getPrescription());
        assertEquals(consultation1.getAppointmentId(), result.getAppointmentId());
        verify(consultationDAO, times(1)).findConsultationByAppointmentId(consultation1.getAppointmentId());
        verify(consultationDAO, times(1)).save(consultation1);
    }

    /**
     * Tests the updateConsultationByAppointmentId method when no consultation is found for the given appointment ID.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testUpdateConsultationByAppointmentId_NotFound() {
        ConsultationDto updatedDto = new ConsultationDto();
        updatedDto.setNotes("Updated Notes");
        updatedDto.setPrescription("Updated Prescription");
        when(consultationDAO.findConsultationByAppointmentId("nonExistingAppId")).thenReturn(Optional.empty());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.updateConsultationByAppointmentId("nonExistingAppId", updatedDto);
        });
        verify(consultationDAO, times(1)).findConsultationByAppointmentId("nonExistingAppId");
        verify(consultationDAO, never()).save(any(Consultation.class));
    }

    /**
     * Tests the deleteConsultation method.
     * Verifies that the method deletes the consultation record.
     */
    @Test
    public void testDeleteConsultation() {
        when(consultationDAO.findByConsultationId(consultation1.getConsultationId())).thenReturn(Optional.of(consultation1));
        consultationService.deleteConsultation(consultation1.getConsultationId());
        verify(consultationDAO, times(1)).findByConsultationId(consultation1.getConsultationId());
        verify(consultationDAO, times(1)).delete(consultation1);
    }

    /**
     * Tests the deleteConsultation method when no consultation is found.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testDeleteConsultation_NotFound() {
        String nonExistingId = UUID.randomUUID().toString();
        when(consultationDAO.findByConsultationId(nonExistingId)).thenReturn(Optional.empty());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.deleteConsultation(nonExistingId);
        });
        verify(consultationDAO, times(1)).findByConsultationId(nonExistingId);
        verify(consultationDAO, never()).delete(any(Consultation.class));
    }

    /**
     * Tests the deleteConsultationByAppointmentId method.
     * Verifies that the method deletes the consultation record by appointment ID.
     */
    @Test
    public void testDeleteConsultationByAppointmentId() {
        when(consultationDAO.findConsultationByAppointmentId(consultation1.getAppointmentId())).thenReturn(Optional.of(consultation1));
        consultationService.deleteConsultationByAppointmentId(consultation1.getAppointmentId());
        verify(consultationDAO, times(1)).findConsultationByAppointmentId(consultation1.getAppointmentId());
        verify(consultationDAO, times(1)).delete(consultation1);
    }

    /**
     * Tests the deleteConsultationByAppointmentId method when no consultation is found for the given appointment ID.
     * Verifies that the method throws NoConsultationDetailsFoundException.
     */
    @Test
    public void testDeleteConsultationByAppointmentId_NotFound() {
        when(consultationDAO.findConsultationByAppointmentId("nonExistingAppId")).thenReturn(Optional.empty());
        assertThrows(NoConsultationDetailsFoundException.class, () -> {
            consultationService.deleteConsultationByAppointmentId("nonExistingAppId");
        });
        verify(consultationDAO, times(1)).findConsultationByAppointmentId("nonExistingAppId");
        verify(consultationDAO, never()).delete(any(Consultation.class));
    }
}