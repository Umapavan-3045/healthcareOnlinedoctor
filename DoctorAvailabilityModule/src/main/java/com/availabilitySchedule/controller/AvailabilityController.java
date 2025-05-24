package com.availabilitySchedule.controller;

import com.availabilitySchedule.dto.AvailabilityDTO;
import com.availabilitySchedule.dto.Response;
import com.availabilitySchedule.service.AvailabilityService;
import com.availabilitySchedule.model.Availability;
import com.availabilitySchedule.model.Specialization;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing availability.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
@RestController
@RequestMapping("/availability")
@Slf4j
@CrossOrigin(origins="http://localhost:3000")
public class AvailabilityController {
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);

	@Autowired
	private AvailabilityService availabilityService;

	/**
	 * GET /AvailabilityId/{availabilityId} : Get availability by ID.
	 *
	 * @param availabilityId the ID of the availability
	 * @return the ResponseEntity with status 200 (OK) and the availability, or
	 *         status 404 (Not Found)
	 */
	@GetMapping("/AvailabilityId/{availabilityId}")
	public ResponseEntity<?> viewById(@PathVariable String availabilityId) {
		log.info("Fetching availability for ID: {}", availabilityId);
		Availability availability = availabilityService.viewById(availabilityId);
		AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
		//Response<?> response = new Response<>(true, HttpStatus.OK, availabilityDto);
		return new ResponseEntity<>(availabilityDto, HttpStatus.OK);
	}

	/**
	 * PUT /update/{availableId}/reschedule/{unavailableId} : Update availability
	 * status.
	 *
	 * @param availableId   the ID of the availability to be marked as available
	 * @param unavailableId the ID of the availability to be marked as unavailable
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@PutMapping("/update/{availableId}/reschedule/{unavailableId}")
	public ResponseEntity<String> updateAvailability(@PathVariable String availableId,
			@PathVariable String unavailableId) {
		log.info("Updating availability status: availableId={}, unavailableId={}", availableId, unavailableId);
		availabilityService.updateAvailabilityStatus(availableId, unavailableId);
		return new ResponseEntity<>( "Update Successfull",HttpStatus.OK);
	}
	
	/**
	 * PUT /cancel/{availableId} : Cancel availability
	 * status.
	 *
	 * @param availableId   the ID of the availability to be marked as available
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@PutMapping("/cancel/{availableId}")
	public ResponseEntity<String> cancelAvailability(@PathVariable String availableId) {
		log.info("Canceling availability status: availableId={}", availableId);
		availabilityService.cancelAvailabilityStatus(availableId);
		return new ResponseEntity<>( "Cancel Successfull",HttpStatus.OK);
	}

	/**
	 * GET /doctor/{doctorId}/date/{date} : Get availability by doctor ID and date.
	 *
	 * @param doctorId the ID of the doctor
	 * @param date     the date of the availability
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         availabilities, or status 404 (Not Found)
	 */
	@GetMapping("/doctor/{doctorId}/date/{date}")
	public List<?> getAvailabilityByDoctorIdAndDate(@PathVariable String doctorId,
			@PathVariable LocalDate date) {
		log.info("Fetching availability for doctorId={} and date={}", doctorId, date);
		List<Availability> availabilities = availabilityService.getAvailabilityByDoctorIdAndDate(doctorId, date);
		List<AvailabilityDTO> availabilityDtos = new ArrayList<AvailabilityDTO>();
		for (Availability availability : availabilities) {
			AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
			availabilityDtos.add(availabilityDto);
		}
		//Response<List<?>> response = new Response<>(true, HttpStatus.OK, availabilityDtos);
		return availabilityDtos;
	}

	/**
	 * GET /specialization/{specialization}/date/{date} : Get availability by
	 * specialization and date.
	 *
	 * @param specialization the specialization of the doctor
	 * @param date           the date of the availability
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         availabilities, or status 404 (Not Found)
	 */
	@GetMapping("/specialization/{specialization}/date/{date}")
	public List<?> getAvailabilityBySpecializationAndDate(
			@PathVariable Specialization specialization, @PathVariable LocalDate date) {
		log.info("Fetching availability for specialization={} and date={}", specialization, date);
		List<Availability> availabilities = availabilityService.getAvailabilityBySpecializationAndDate(specialization,
				date);
		List<AvailabilityDTO> availabilityDtos = new ArrayList<AvailabilityDTO>();
		for (Availability availability : availabilities) {
			AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
			availabilityDtos.add(availabilityDto);
		}
		//Response<List<?>> response = new Response<>(true, HttpStatus.OK, availabilityDtos);
		return availabilityDtos;
	}

	/**
	 * GET /doctor/{doctorId}/date-range : Get availability by doctor ID and date
	 * range.
	 * 
	 * @param doctorId  the ID of the doctor
	 * @param startDate the start date of the range
	 * @param endDate   the end date of the range
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         availabilities, or status 404 (Not Found)
	 */
	@GetMapping("/doctor/{doctorId}/date-range")
	public List<?> getAvailabilityByDoctorIdAndDateRange(@PathVariable String doctorId,
			@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
		log.info("Fetching availability for doctorId={}, startDate={}, endDate={}", doctorId, startDate, endDate);
		List<Availability> availabilities = availabilityService.getAvailabilityByDoctorIdAndDateRange(doctorId,
				startDate, endDate);
		
		return availabilities;
	}

	/**
	 * GET /specialization/{specialization}/date-range : Get availability by
	 * specialization and date range.
	 * 
	 * @param specialization the specialization of the doctor
	 * @param startDate      the start date of the range
	 * @param endDate        the end date of the range
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         availabilities, or status 404 (Not Found)
	 */
	@GetMapping("/specialization/{specialization}/date-range")
	public List<?> getAvailabilityBySpecializationAndDateRange(
			@PathVariable Specialization specialization, @RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate) {
		log.info("Fetching availability for specialization={}, startDate={}, endDate={}", specialization, startDate,
				endDate);
		List<Availability> availabilities = availabilityService
				.getAvailabilityBySpecializationAndDateRange(specialization, startDate, endDate);
		List<AvailabilityDTO> availabilityDtos = new ArrayList<AvailabilityDTO>();
		for (Availability availability : availabilities) {
			AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
			availabilityDtos.add(availabilityDto);
		}
		//Response<List<?>> response = new Response<>(true, HttpStatus.OK, availabilityDtos);
		return availabilityDtos;
	}

	/**
	 * GET /doctors : Get all availabilities.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of all
	 *         availabilities
	 */
	@GetMapping("/doctors")
	public ResponseEntity<Response<List<?>>> viewAllAvailabilities() {
		log.info("Fetching all availabilities.");
		List<Availability> availabilities = availabilityService.viewAllAvailabilities();
		List<AvailabilityDTO> availabilityDtos = new ArrayList<AvailabilityDTO>();
		for (Availability availability : availabilities) {
			AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
			availabilityDtos.add(availabilityDto);
		}
		Response<List<?>> response = new Response<>(true, HttpStatus.OK, availabilityDtos);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * PUT /block/{availabilityId} : Block a time slot.
	 *
	 * @param availabilityId the ID of the availability to be blocked
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@PutMapping("/block/{availabilityId}")
	public ResponseEntity<Response<?>> blockTimeSlot(@PathVariable String availabilityId) {
		log.info("Blocking time slot with ID: {}", availabilityId);
		availabilityService.blockTimeSlot(availabilityId);
		Response<?> response = new Response<>(true, HttpStatus.OK, "Time slot booked successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * PUT /block/{availabilityId} : Block a time slot.
	 *
	 * @param availabilityId the ID of the availability to be blocked
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@PutMapping("/book/{availabilityId}")
	public ResponseEntity<Response<?>> bookTimeSlot(@PathVariable String availabilityId) {
		log.info("Booking time slot with ID: {}", availabilityId);
		availabilityService.bookTimeSlot(availabilityId);
		Response<?> response = new Response<>(true, HttpStatus.OK, "Time slot booked successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * DELETE /delete/{availabilityId} : Delete an availability.
	 *
	 * @param availabilityId the ID of the availability to be deleted
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@DeleteMapping("/delete/{availabilityId}")
	public void deleteAvailability(@PathVariable String availabilityId) {
		log.info("Deleting availability with ID: {}", availabilityId);
		availabilityService.deleteAvailability(availabilityId);
	}
	
	@DeleteMapping("/deletebydoctorId/{doctorId}")
	public void deleteAllAvailability(@PathVariable String doctorId){
	    System.out.println("hello delete doctor");
	    availabilityService.deleteByDoctorId(doctorId);
	}
	/**
	 * PUT /release/{availabilityId} : Release an availability.
	 *
	 * @param availabilityId the ID of the availability to be released
	 * @return the ResponseEntity with status 200 (OK) and a success message
	 */
	@PutMapping("/release/{availabilityId}")
	public ResponseEntity<Response<?>> releaseAvailabilityById(@PathVariable String availabilityId) {
		log.info("Releasing availability with ID: {}", availabilityId);
		availabilityService.releaseAvailabilityById(availabilityId);
		Response<?> response = new Response<>(true, HttpStatus.OK, null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}	
		@GetMapping("/doctor/{doctorId}")
		public List<?> getAvailabilityByDoctorId(@PathVariable String doctorId) {
			log.info("Fetching availability for doctorId={} and date={}", doctorId);
			List<Availability> availabilities = availabilityService.getAvailabilityByDoctorId(doctorId);
			List<AvailabilityDTO> availabilityDtos = new ArrayList<AvailabilityDTO>();
			for (Availability availability : availabilities) {
				AvailabilityDTO availabilityDto = AvailabilityDTO.fromEntity(availability);
				availabilityDtos.add(availabilityDto);
			}
			return availabilityDtos;
		}
		
		/**
		 * POST /release/{availabilityId} : Release an availability.
		 *
		 * @param availabilityId the ID of the availability to be released
		 * @return the ResponseEntity with status 200 (OK) and a success message
		 */
		@PostMapping("/create/doctor/{doctorId}/{doctorName}/{specialization}")
		public void createAvailabilityForDoctorId(@PathVariable String doctorId,
				@PathVariable String doctorName, @PathVariable Specialization specialization) {
			
			System.out.println(doctorId+" "+doctorName+" "+specialization);
			log.info("Creating Availabilities for Doctor ID: {}", doctorId);
			availabilityService.createAvailabilityForDoctorId(doctorId, doctorName, specialization);
		}
	
	
}