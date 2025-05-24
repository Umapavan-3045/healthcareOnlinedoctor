package com.healthcare.management.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.management.dto.ConsultationDto;
import com.healthcare.management.dto.Response;
import com.healthcare.management.entity.Consultation;
import com.healthcare.management.exception.NoConsultationDetailsFoundException;
import com.healthcare.management.service.ConsultationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * ConsultationController is a REST controller that handles HTTP requests for
 * managing consultation records. It provides end points for creating,
 * retrieving, updating, and deleting consultation records.
 * 
 * @RestController - Indicates that this class is a REST
 *                 controller. @RequestMapping("/consultation") - Maps HTTP
 *                 requests to /consultation to this controller.
 * @Slf4j - Provides logging capabilities.
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/consultation")
public class ConsultationController {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);

	@Autowired
	private ConsultationService consultantService;

	/**
	 * Creates a new consultation record.
	 * 
	 * @param consultationDto - The consultation data transfer object containing the
	 *                        details of the consultation to be created.
	 * @return ResponseEntity<Response<?>> - The response entity containing the
	 *         created consultation details or an error message.
	 */

	@PostMapping("/create")
	public ResponseEntity<Response<?>> addConsultation(@RequestBody @Valid ConsultationDto consultationDto) {
		try {
			log.info("Creating a new consultation record..");
			Consultation consultation = consultantService.createConsultation(consultationDto);
			ConsultationDto responseDto = new ConsultationDto(consultation.getConsultationId(),
					consultation.getAppointmentId(), consultation.getNotes(), consultation.getPrescription());
			Response<ConsultationDto> response = new Response<>(true, HttpStatus.CREATED, responseDto, null);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception ex) {
			log.error("Exception: {}", ex.getMessage());
			Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	

	/**
	 * Retrieves all consultation records.
	 * 
	 * @return ResponseEntity<Response<?>> - The response entity containing the list
	 *         of all consultation records or an error message.
	 */

	@GetMapping
	public ResponseEntity<Response<?>> getConsultationDetails() {
		try {
			log.info("Loading all the consultation details...");
			List<ConsultationDto> consultationList = consultantService.getAllConsultationDetails().stream()
					.map(consultation -> new ConsultationDto(consultation.getConsultationId(),
							consultation.getAppointmentId(), consultation.getNotes(), consultation.getPrescription()))
					.collect(Collectors.toList());
			Response<List<ConsultationDto>> response = new Response<>(true, HttpStatus.OK, consultationList, null);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Exception: {}", ex.getMessage());
			Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves consultation records filtered by Patient ID.
	 * 
	 * @param appId - The appointment ID to filter consultation records.
	 * @return ResponseEntity<Response<?>> - The response entity containing the list
	 *         of consultation records filtered by Patient ID or an error message.
	 */

	@GetMapping("/appointment/{appId}")
	public ResponseEntity<Response<?>> getConsultationDetailsByAppointmentID(@PathVariable String appId) {
	    try {
	        log.info("Retrieving the consultation details filtered by Appointment ID..");
	        List<ConsultationDto> consultationList = consultantService.findConDetailsByAppId(appId).stream()
	            .map(consultation -> new ConsultationDto(
	                consultation.getConsultationId(),
	                consultation.getAppointmentId(),
	                consultation.getNotes(),
	                consultation.getPrescription()
	            ))
	            .collect(Collectors.toList());

	        Response<List<ConsultationDto>> response = new Response<>(true, HttpStatus.OK, consultationList, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (NoConsultationDetailsFoundException ex) {  
	        log.warn("No consultation found for Appointment ID: {}", appId);
	        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage()); // ✅ Set status to 404
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

	    } catch (Exception ex) {  
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	/**
	 * Updates an existing consultation record.
	 * 
	 * @param conId           - The consultation ID of the record to be updated.
	 * @param consultationDto - The consultation data transfer object containing the
	 *                        updated details of the consultation.
	 * @return ResponseEntity<Response<?>> - The response entity containing the
	 *         updated consultation details or an error message.
	 */

	@PutMapping("/update/consul/{conId}")
	public ResponseEntity<Response<?>> updateConsultation(@PathVariable String conId,
			@RequestBody @Valid ConsultationDto consultationDto) {
		try {
			log.info("Updating Consultation Details..");
			Consultation consultation = consultantService.updateConsultationDetailsById(conId, consultationDto);
			ConsultationDto responseDto = new ConsultationDto(consultation.getConsultationId(),
					consultation.getAppointmentId(), consultation.getNotes(), consultation.getPrescription());
			Response<ConsultationDto> response = new Response<>(true, HttpStatus.OK, responseDto, null);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Exception: {}", ex.getMessage());
			Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/** Updates consultation by Appointment ID */	
	@PutMapping("/update/appointment/{appId}")
	public ResponseEntity<Response<?>> updateConsultationByAppointmentID(
	    @PathVariable String appId, @RequestBody @Valid ConsultationDto consultationDto) {

	    try {
	        log.info("Updating consultation for Appointment ID: {}", appId);
	        Consultation consultation = consultantService.updateConsultationByAppointmentId(appId, consultationDto);
	        
	        ConsultationDto responseDto = new ConsultationDto(
	            consultation.getConsultationId(),
	            consultation.getAppointmentId(),
	            consultation.getNotes(),
	            consultation.getPrescription()
	        );
	        
	        Response<ConsultationDto> response = new Response<>(true, HttpStatus.OK, responseDto, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (NoConsultationDetailsFoundException ex) { 
	        log.warn("No consultation found for Appointment ID: {}", appId);
	        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    } catch (Exception ex) { 
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	/**
	 * Deletes a consultation record.
	 * 
	 * @param conId - The consultation ID of the record to be deleted.
	 * @return ResponseEntity<Response<?>> - The response entity indicating the
	 *         result of the delete operation or an error message.
	 */

	@DeleteMapping("/delete/consul/{conId}")
	public ResponseEntity<Response<?>> removeConsultationDetails(@PathVariable String conId) {
		try {
			log.info("Deleting the consultation details..");
			consultantService.deleteConsultation(conId);
			log.info("Successfully removed the consultation entry..");
			Response<String> response = new Response<>(true, HttpStatus.OK, "Consultation deleted successfully", null);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Exception: {}", ex.getMessage());
			Response<Void> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/** Delete consultation details by Appointment ID */
	@CrossOrigin(origins = "*")
	@DeleteMapping("/delete/appointment/{appId}")
	public ResponseEntity<Response<?>> deleteConsultationByAppointmentID(@PathVariable String appId) {
	    try {
	        log.info("Deleting consultation for Appointment ID: {}", appId);
	        consultantService.deleteConsultationByAppointmentId(appId);
	        log.info("Successfully removed the consultation entry..");
	        Response<String> response = new Response<>(true, HttpStatus.OK, "Consultation deleted successfully!", null);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (NoConsultationDetailsFoundException ex) {
	        log.warn("No consultation found for Appointment ID: {}", appId);
	        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    } catch (Exception ex) {
	        log.error("Exception: {}", ex.getMessage());
	        Response<?> response = new Response<>(false, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	
	
	
}