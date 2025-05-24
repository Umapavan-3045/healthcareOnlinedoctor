package com.healthcare.management.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.healthcare.management.dao.HistoryDAO;
import com.healthcare.management.dao.PatientDAO;
import com.healthcare.management.dto.HistoryDto;
import com.healthcare.management.entity.MedicalHistory;
import com.healthcare.management.entity.Patient;
import com.healthcare.management.exception.NoHistoryFoundException;
import com.healthcare.management.exception.NoPatientFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * HistoryService is a service class that provides methods for managing medical history records.
 * It includes methods for creating, retrieving, updating, and deleting medical history records.
 * 
 * @Service - Indicates that this class is a service component in the Spring framework.
 * @Slf4j - Provides logging capabilities.
 */
@Slf4j
@Service
public class HistoryService {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);
	
	@Autowired
	private HistoryDAO historyDAO;
	
	@Autowired
	private PatientDAO patientDAO;

	/*public HistoryDto addHistory(HistoryDto historyDTO) {
	    log.info("Added a new record in medical history table..");
	    MedicalHistory medicalHistory = new MedicalHistory();
	    medicalHistory.setHistory_id(historyDTO.getHistoryId());
	    medicalHistory.setPatientId(historyDTO.getPatientId());
	    medicalHistory.setHealthHistory(historyDTO.getHealthHistory());

//	    Patient patient = patientDAO.findById(historyDTO.getPatientId())
//	            .orElseThrow(() -> new NoPatientFoundException("No Patient exists with the ID " + historyDTO.getPatientId()));
//	    medicalHistory.setPatientId(patient);

	    MedicalHistory savedMedicalHistory = historyDAO.save(medicalHistory);

	    HistoryDto historyDto = new HistoryDto();
	    historyDto.setHistoryId(savedMedicalHistory.getHistory_id());
	    historyDto.setPatientId(savedMedicalHistory.getPatientId());
	    historyDto.setHealthHistory(savedMedicalHistory.getHealthHistory());
	    return historyDto;
	}  */
	
	/**
	 * Creates a new medical history record.
	 * 
	 * @param historyDTO - The history data transfer object containing the details of the medical history to be created.
	 * @return HistoryDto - The created medical history details.
	 * @throws NoPatientFoundException - If no patient is found for the given patient ID.
	 */
	public HistoryDto addHistory(HistoryDto historyDTO) {
	    log.info("Added a new record in medical history table..");
	    MedicalHistory medicalHistory = new MedicalHistory();
	    medicalHistory.setHistoryId(historyDTO.getHistoryId());
	    medicalHistory.setPatientId(historyDTO.getPatientId());
	    medicalHistory.setHealthHistory(historyDTO.getHealthHistory());

	    // Check if the patient exists
//	    Patient patient = patientDAO.findById(historyDTO.getPatientId())
//	            .orElseThrow(() -> new NoPatientFoundException("No Patient exists with the ID " + historyDTO.getPatientId()));
	    // medicalHistory.setPatientId(patient); 

	    MedicalHistory savedMedicalHistory = historyDAO.save(medicalHistory);

	    HistoryDto historyDto = new HistoryDto();
	    historyDto.setHistoryId(savedMedicalHistory.getHistoryId());
	    historyDto.setPatientId(savedMedicalHistory.getPatientId());
	    historyDto.setHealthHistory(savedMedicalHistory.getHealthHistory());
	    log.info("Medical history created successfully!");
	    return historyDto;
	}
	/**
	 * Retrieves medical history details by history ID.
	 * 
	 * @param historyId - The ID of the medical history to be retrieved.
	 * @return HistoryDto - The medical history details.
	 * @throws NoHistoryFoundException - If no medical history is found for the given history ID.
	 */
	public HistoryDto getMedicalHistoryByHistoryId(String historyId) {
        log.info("Retrieving medical history details of patient with history id " + historyId);
        MedicalHistory medicalHistory = historyDAO.findById(historyId)
                .orElseThrow(() -> new NoHistoryFoundException("No Medical History exists with the history Id " + historyId));
        
        HistoryDto historyDto = new HistoryDto();
        historyDto.setHistoryId(medicalHistory.getHistoryId());
        historyDto.setHealthHistory(medicalHistory.getHealthHistory());
        historyDto.setPatientId(medicalHistory.getPatientId());
        
        return historyDto;
    
    }
	
	/**
	 * Retrieves medical history details by patient ID.
	 * 
	 * @param patientId - The patient ID to filter medical history records.
	 * @return HistoryDto - The medical history details.
	 * @throws NoHistoryFoundException - If no medical history is found for the given patient ID.
	 */
	
	/*public HistoryDto getHistoryByPatientId(String patientId) {
		log.info("Retrieving medical history details of patient with patient id " + patientId);
        MedicalHistory medicalHistory = historyDAO.getMedicalHistoryByPatientId(patientId);
        
        if (medicalHistory == null) {
            throw new NoHistoryFoundException("No Medical History exists for Patient ID: " + patientId);
        }
        
        HistoryDto historyDto = new HistoryDto();
        historyDto.setHistoryId(medicalHistory.getHistoryId());
        historyDto.setHealthHistory(medicalHistory.getHealthHistory());
        historyDto.setPatientId(medicalHistory.getPatientId());
        
        return historyDto;
    }*/
	
	 public List<HistoryDto> getHistoryByPatientId(String patientId) {
	        log.info("Retrieving medical history details of patient with patient id " + patientId);
	        
//	        Patient patient = patientDAO.findById(patientId)
//	                
	        
	        List<MedicalHistory> medicalHistories = historyDAO.getMedicalHistoryByPatientId(patientId);
	        
	        if (medicalHistories.isEmpty()) {
	            throw new NoHistoryFoundException("No Medical History exists for Patient ID: " + patientId);
	        }
	        
	        List<HistoryDto> historyDtos = new ArrayList<>();
	        for (MedicalHistory medicalHistory : medicalHistories) {
	            HistoryDto historyDto = new HistoryDto();
	            historyDto.setHistoryId(medicalHistory.getHistoryId());
	            historyDto.setHealthHistory(medicalHistory.getHealthHistory());
	            historyDto.setPatientId(medicalHistory.getPatientId());
	            historyDtos.add(historyDto);
	        }
	        
	        return historyDtos;
	    }
	
	/**
	 * Updates an existing medical history record by patient ID.
	 * 
	 * @param patientId - The patient ID of the record to be updated.
	 * @param medicalHistoryDTO - The history data transfer object containing the updated details of the medical history.
	 * @return HistoryDto - The updated medical history details.
	 * @throws NoHistoryFoundException - If no medical history is found for the given patient ID.
	 */
	
	/*public HistoryDto updateMedicalHistory(String patientId, HistoryDto medicalHistoryDTO) {
        MedicalHistory medicalHistory = historyDAO.getMedicalHistoryByPatientId(patientId);
        if (medicalHistory == null) {
            throw new NoHistoryFoundException("No medical history exists for Patient ID: " + patientId);
        }
        medicalHistory.setHealthHistory(medicalHistoryDTO.getHealthHistory());
        log.info("Successfully Updated the medical history of Patient with id " + patientId);
        MedicalHistory updatedMedicalHistory = historyDAO.save(medicalHistory);
        
        HistoryDto historyDto = new HistoryDto();
        historyDto.setHistoryId(updatedMedicalHistory.getHistoryId());
        historyDto.setHealthHistory(updatedMedicalHistory.getHealthHistory());
        historyDto.setPatientId(updatedMedicalHistory.getPatientId());
        
        return historyDto;
    }*/
	 
	 

	
	/**
	 * Deletes a medical history record by patient ID.
	 * 
	 * @param patientId - The patient ID of the record to be deleted.
	 * @throws NoHistoryFoundException - If no medical history is found for the given patient ID.
	 */
	
	 public void deleteMedicalHistory(String patientId) {
		    log.info("Deleting medical history for patient ID: {}", patientId);
		    List<MedicalHistory> medicalHistories = historyDAO.getMedicalHistoryByPatientId(patientId);
		    
		    if (medicalHistories.isEmpty()) {
		        throw new NoHistoryFoundException("No Medical History exists for Patient ID: " + patientId);
		    }
		    
		    historyDAO.deleteAll(medicalHistories);
		    log.info("Successfully deleted the medical history of Patient with id " + patientId);
		}
	

}
