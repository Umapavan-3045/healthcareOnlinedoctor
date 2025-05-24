package com.healthcare.management.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.healthcare.management.dao.HistoryDAO;
import com.healthcare.management.dao.PatientDAO;
import com.healthcare.management.dto.HistoryDto;
import com.healthcare.management.entity.MedicalHistory;
import com.healthcare.management.exception.NoHistoryFoundException;
import com.healthcare.management.service.HistoryService;

/**
 * MedicalHistoryTest is a test class for the HistoryService.
 * It uses Mockito to mock dependencies and JUnit 5 for testing.
 */
public class MedicalHistoryTest {

    @Mock
    private HistoryDAO historyDAO;

    @Mock
    private PatientDAO patientDAO;

    @InjectMocks
    private HistoryService historyService;

    private MedicalHistory history1;
    private MedicalHistory history2;
    private HistoryDto historyDto;

    public HistoryDAO getHistoryDAO() {
		return historyDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public PatientDAO getPatientDAO() {
		return patientDAO;
	}

	public void setPatientDAO(PatientDAO patientDAO) {
		this.patientDAO = patientDAO;
	}

	public HistoryService getHistoryService() {
		return historyService;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	public MedicalHistory getHistory1() {
		return history1;
	}

	public void setHistory1(MedicalHistory history1) {
		this.history1 = history1;
	}

	public MedicalHistory getHistory2() {
		return history2;
	}

	public void setHistory2(MedicalHistory history2) {
		this.history2 = history2;
	}

	public HistoryDto getHistoryDto() {
		return historyDto;
	}

	public void setHistoryDto(HistoryDto historyDto) {
		this.historyDto = historyDto;
	}

	@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        history1 = new MedicalHistory();
        history1.setHistoryId(UUID.randomUUID().toString());
        history1.setPatientId("patient1");
        history1.setHealthHistory("Flu, Asthma");

        history2 = new MedicalHistory();
        history2.setHistoryId(UUID.randomUUID().toString());
        history2.setPatientId("patient1");
        history2.setHealthHistory("Allergies");

        historyDto = new HistoryDto();
        historyDto.setPatientId("9813f8ba-2332-4a3b-b8bc-2ad0750bbb4f");
        historyDto.setHealthHistory("Diabetes");
    }

    /**
     * Tests the addHistory method.
     * Verifies that a new medical history record is created and the UUID is set.
     */
    @Test
    public void testAddHistory() {
        MedicalHistory savedHistory = new MedicalHistory();
        savedHistory.setHistoryId(UUID.randomUUID().toString()); // Simulate UUID generation on save
        savedHistory.setPatientId(historyDto.getPatientId());
        savedHistory.setHealthHistory(historyDto.getHealthHistory());
        when(historyDAO.save(any(MedicalHistory.class))).thenReturn(savedHistory);

     
        HistoryDto result = historyService.addHistory(historyDto);

        assertNotNull(result);
        assertEquals(savedHistory.getHistoryId(), result.getHistoryId());
        assertEquals(savedHistory.getPatientId(), result.getPatientId());
        assertEquals(savedHistory.getHealthHistory(), result.getHealthHistory());
        verify(historyDAO, times(1)).save(any(MedicalHistory.class));
        verify(patientDAO, never()).findById(anyString()); // Ensure patientDAO.findById is never called
    }

    

    /**
     * Tests the getMedicalHistoryByHistoryId method.
     * Verifies that the method returns the correct medical history record.
     */
    @Test
    public void testGetMedicalHistoryByHistoryId() {
        when(historyDAO.findById(history1.getHistoryId())).thenReturn(Optional.of(history1));
        HistoryDto result = historyService.getMedicalHistoryByHistoryId(history1.getHistoryId());
        assertNotNull(result);
        assertEquals(history1.getHistoryId(), result.getHistoryId());
        assertEquals(history1.getHealthHistory(), result.getHealthHistory());
        assertEquals(history1.getPatientId(), result.getPatientId());
        verify(historyDAO, times(1)).findById(history1.getHistoryId());
    }

    /**
     * Tests the getMedicalHistoryByHistoryId method when no medical history is found.
     * Verifies that the method throws NoHistoryFoundException.
     */
    @Test
    public void testGetMedicalHistoryByHistoryId_NotFound() {
        String nonExistingId = UUID.randomUUID().toString();
        when(historyDAO.findById(nonExistingId)).thenReturn(Optional.empty());
        assertThrows(NoHistoryFoundException.class, () -> {
            historyService.getMedicalHistoryByHistoryId(nonExistingId);
        });
        verify(historyDAO, times(1)).findById(nonExistingId);
    }

    /**
     * Tests the getHistoryByPatientId method.
     * Verifies that the method returns the correct list of medical history records for a given patient ID.
     */
    @Test
    public void testGetHistoryByPatientId() {
        List<MedicalHistory> medicalHistories = Arrays.asList(history1, history2);
        when(historyDAO.getMedicalHistoryByPatientId(history1.getPatientId())).thenReturn(medicalHistories);
        List<HistoryDto> result = historyService.getHistoryByPatientId(history1.getPatientId());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(history1.getHistoryId(), result.get(0).getHistoryId());
        assertEquals(history1.getHealthHistory(), result.get(0).getHealthHistory());
        assertEquals(history1.getPatientId(), result.get(0).getPatientId());
        assertEquals(history2.getHistoryId(), result.get(1).getHistoryId());
        assertEquals(history2.getHealthHistory(), result.get(1).getHealthHistory());
        assertEquals(history2.getPatientId(), result.get(1).getPatientId());
        verify(historyDAO, times(1)).getMedicalHistoryByPatientId(history1.getPatientId());
    }

    /**
     * Tests the getHistoryByPatientId method when no medical history is found for the given patient ID.
     * Verifies that the method throws NoHistoryFoundException.
     */
    @Test
    public void testGetHistoryByPatientId_NotFound() {
        String nonExistingPatientId = "nonExistingPatient";
        when(historyDAO.getMedicalHistoryByPatientId(nonExistingPatientId)).thenReturn(new ArrayList<>());
        assertThrows(NoHistoryFoundException.class, () -> {
            historyService.getHistoryByPatientId(nonExistingPatientId);
        });
        verify(historyDAO, times(1)).getMedicalHistoryByPatientId(nonExistingPatientId);
    }

    /**
     * Tests the deleteMedicalHistory method.
     * Verifies that the method deletes all medical history records for a given patient ID.
     */
    @Test
    public void testDeleteMedicalHistory() {
        List<MedicalHistory> medicalHistoriesToDelete = Arrays.asList(history1, history2);
        when(historyDAO.getMedicalHistoryByPatientId(history1.getPatientId())).thenReturn(medicalHistoriesToDelete);
        doNothing().when(historyDAO).deleteAll(medicalHistoriesToDelete);

        historyService.deleteMedicalHistory(history1.getPatientId());

        verify(historyDAO, times(1)).getMedicalHistoryByPatientId(history1.getPatientId());
        verify(historyDAO, times(1)).deleteAll(medicalHistoriesToDelete);
    }

    /**
     * Tests the deleteMedicalHistory method when no medical history is found for the given patient ID.
     * Verifies that the method throws NoHistoryFoundException.
     */
    @Test
    public void testDeleteMedicalHistory_NotFound() {
        String nonExistingPatientId = "nonExistingPatient";
        when(historyDAO.getMedicalHistoryByPatientId(nonExistingPatientId)).thenReturn(new ArrayList<>());
        assertThrows(NoHistoryFoundException.class, () -> {
            historyService.deleteMedicalHistory(nonExistingPatientId);
        });
        verify(historyDAO, times(1)).getMedicalHistoryByPatientId(nonExistingPatientId);
        verify(historyDAO, never()).deleteAll(any());
    }
}