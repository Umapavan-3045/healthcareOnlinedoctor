package com.healthcare.management.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.management.dto.HistoryDto;
import com.healthcare.management.dto.Response;
//import com.healthcare.management.entity.MedicalHistory;
import com.healthcare.management.service.HistoryService;

import lombok.extern.slf4j.Slf4j;
/**
 * HistoryController is a REST controller that handles HTTP requests for managing medical history records.
 * It provides end points for creating, retrieving, updating, and deleting medical history records.
 * 
 * @RestController - Indicates that this class is a REST controller.
 * @RequestMapping("/history") - Maps HTTP requests to /history to this controller.
 * @Slf4j - Provides logging capabilities.
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/history")
public class HistoryController {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);
	
	@Autowired
	private HistoryService historyService;
	
	/**
	 * Creates a new medical history record.
	 * 
	 * @param historyDto - The history data transfer object containing the details of the medical history to be created.
	 * @return ResponseEntity<Response<?>> - The response entity containing the created medical history details or an error message.
	 */
	@PostMapping("/create")
	public ResponseEntity<Response<?>> addMedicalHistory(@RequestBody @Valid HistoryDto historyDto) {
	    try {
	        log.info("Creating a new medical history record..");
	        HistoryDto createdHistory = historyService.addHistory(historyDto);
	        Response<HistoryDto> response = new Response<>(true, HttpStatus.CREATED, createdHistory, null);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	/**
	 * Retrieves medical history records for a specific patient ID.
	 * 
	 * @param patientId - The patient ID to filter medical history records.
	 * @return ResponseEntity<Response<?>> - The response entity containing the medical history details or an error message.
	 */
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<Response<?>> getHistory(@PathVariable String patientId) {
	    try {
	        log.info("Fetching medical history for patient ID: {}", patientId);
	        List<HistoryDto> histories = historyService.getHistoryByPatientId(patientId);
	        Response<List<HistoryDto>> response = new Response<>(true, HttpStatus.OK, histories, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	/**
	 * Retrieves medical history records by history ID.
	 * 
	 * @param historyId - The history ID to filter medical history records.
	 * @return ResponseEntity<Response<?>> - The response entity containing the medical history details or an error message.
	 */

	@GetMapping("/his/{historyId}")
	public ResponseEntity<Response<?>> getHistoryByHistoryId(@PathVariable String historyId) {
	    try {
	        log.info("Fetching medical history for history ID: {}", historyId);
	        HistoryDto history = historyService.getMedicalHistoryByHistoryId(historyId);
	        Response<HistoryDto> response = new Response<>(true, HttpStatus.OK, history, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	/**
	 * Updates an existing medical history record.
	 * 
	 * @param patientId - The patient ID of the record to be updated.
	 * @param historyDto - The history data transfer object containing the updated details of the medical history.
	 * @return ResponseEntity<Response<?>> - The response entity containing the updated medical history details or an error message.
	 */

	/*@PutMapping("patient/{patientId}")
	public ResponseEntity<Response<?>> updateHistory(@PathVariable String patientId, @RequestBody @Valid HistoryDto historyDto) {
	    try {
	        log.info("Updating medical history for patient ID: {}", patientId);
	        HistoryDto updatedHistory = historyService.updateMedicalHistory(patientId, historyDto);
	        Response<HistoryDto> response = new Response<>(true, HttpStatus.OK, updatedHistory, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}*/

	/**
	 * Deletes a medical history record.
	 * 
	 * @param patientID - The patient ID of the record to be deleted.
	 * @return ResponseEntity<Response<?>> - The response entity indicating the result of the delete operation or an error message.
	 */
	/*@DeleteMapping("/delete/patient/{patientID}")
	public ResponseEntity<Response<?>> deleteHistory(@PathVariable String patientID) {
	    try {
	        log.info("Deleting medical history for patient ID: {}", patientID);
	        historyService.deleteMedicalHistory(patientID);
	        Response<?> response = new Response<>(true, HttpStatus.NO_CONTENT, null, null);
	        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}*/
	
	@DeleteMapping("/delete/patient/{patientId}")
	public ResponseEntity<Response<?>> deleteHistory(@PathVariable String patientId) {
	    try {
	        log.info("Deleting medical history for patient ID: {}", patientId);
	        historyService.deleteMedicalHistory(patientId);
	        Response<?> response = new Response<>(true, HttpStatus.OK, null, "Successfully deleted medical history for patient ID: " + patientId);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
}
