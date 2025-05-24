package com.users.app.controller;

import java.util.Arrays;
import java.util.List;
//import java.util.Optional;
//
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.users.app.dto.ApiResponse;
import com.users.app.dto.DoctorAvailabilityDto;
import com.users.app.dto.DoctorDto;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.DoctorNotFoundException;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.model.Doctor;
import com.users.app.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class DoctorController {
	@Autowired
	private DoctorService doctorService;
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<?>> getDoctor(@PathVariable String id) {
		try {
		DoctorDto doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Doctor Details", doctor));
		}
		catch(DoctorNotFoundException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
	}
	
	@PatchMapping("/update")
	public ResponseEntity<ApiResponse<?>> updateDoctor(@RequestBody @Valid DoctorDto doc){
		
		try {
			doctorService.updateDoctorDetails(doc);
			return ResponseEntity.ok(new ApiResponse<>(true,"Doctor Details Updated Successfully", null));
		}
		catch(DoctorNotFoundException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
		catch(PhoneNumberAlreadyExistsException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
		catch(EmailAlreadyExistsException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
		
	}
	
	@GetMapping("/doctorSearch")
	public ResponseEntity<ApiResponse<?>> getDoctors(@RequestParam String specialization) {
	    try {
	        // Check if the specialization is present in the Specialization enum
	        boolean isValidSpecialization = Arrays.stream(Specialization.values())
	                                              .anyMatch(s -> s.name().equalsIgnoreCase(specialization));

	        if (!isValidSpecialization) {
	            throw new IllegalArgumentException("Invalid Specialization: " + specialization);
	        }

	        // Convert the string to the corresponding enum value
	        Specialization specEnum = Specialization.valueOf(specialization);

	        // Fetch doctors based on the specialization
	        List<Doctor> doctors = doctorService.getDoctorBySpecialization(specEnum);

	        // Return the list of doctors with an OK status
			return ResponseEntity.ok(new ApiResponse<>(true,"Doctor Details", doctors));
	    } catch (DoctorNotFoundException e) {
	        // Handle case where no doctors are found
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
	    } catch (IllegalArgumentException e) {
	        // Handle case where the specialization string does not match any enum value
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
	    }
	}
	
	@GetMapping("/alldoctors")
	public List<DoctorAvailabilityDto> getAllDoctors(){
		try {
			List<DoctorAvailabilityDto> doctors = doctorService.getAllDoctors();
			return doctors;
		}
		catch(DoctorNotFoundException e) {
			e.printStackTrace();
			return null;
//        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
	}
	
	@GetMapping("/alldoctorDetails")
	public ResponseEntity<ApiResponse<?>> getAllDoctorsDetails(){
		try {
			List<Doctor> doctors = doctorService.getAllDoctorsdetails();
			return ResponseEntity.ok(new ApiResponse<>(true,"Doctor Details", doctors));
		}
		catch(DoctorNotFoundException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
	}
}
