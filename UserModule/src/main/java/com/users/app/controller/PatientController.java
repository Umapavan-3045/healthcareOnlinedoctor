package com.users.app.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.users.app.dto.ApiResponse;
import com.users.app.dto.PatientDto;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.exceptions.UserNotFoundException;
import com.users.app.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class PatientController {		
		
	@Autowired
	private PatientService patientService;
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<?>> getPatient(@PathVariable String id){
		try {
		PatientDto patient = patientService.getPatientById(id);
		return ResponseEntity.ok(new ApiResponse<>(true,"Patient Details", patient));
		}
		catch(UserNotFoundException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
	}
	
	@PatchMapping("/update")
	public ResponseEntity<ApiResponse<String>> updatePatient(@RequestBody @Valid PatientDto pat){
		
		try {
			patientService.updatePatientDetails(pat);
			return ResponseEntity.ok(new ApiResponse<>(true,"Patient Details Updated Successfully", null));
		}
		catch(UserNotFoundException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
		catch(PhoneNumberAlreadyExistsException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
		catch(EmailAlreadyExistsException e) {
        	return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
		}
	}
	
	
}
