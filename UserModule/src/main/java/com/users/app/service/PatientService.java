package com.users.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.users.app.dto.DoctorDto;
import com.users.app.dto.PatientDto;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.DoctorNotFoundException;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.exceptions.UserNotFoundException;
import com.users.app.model.Doctor;
import com.users.app.model.Patient;
import com.users.app.model.User;
import com.users.app.repository.PatientRepository;

@Service
public class PatientService {
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private UserService userService;
	
	@Lazy
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public PatientDto getPatientById(String id){
		Optional<Patient> patient = patientRepository.findBypatientId(id);
		if(!patient.isPresent()) {
			throw new UserNotFoundException("Patient Not Found with Id :"+id);
		}
		Optional<User> user = userService.getUserById(id);
		PatientDto patientDto = new PatientDto();
		
		patientDto.setName(patient.get().getName());
		patientDto.setAge(patient.get().getAge());
		patientDto.setAddress(patient.get().getAddress());
		patientDto.setPatient_id(patient.get().getPatientId());
		patientDto.setEmail(user.get().getEmail());
		patientDto.setPassword(user.get().getPassword());
		patientDto.setGender(patient.get().getGender());
		patientDto.setPhoneNumber(user.get().getPhoneNumber());
			return patientDto;
	}
	
	public void updatePatientDetails(PatientDto patUpdate) {
		Optional<Patient> pat = patientRepository.findBypatientId(patUpdate.getPatient_id());
		if(!pat.isPresent()) {
			throw new UserNotFoundException("Patient Not Found with Id :"+patUpdate.getPatient_id());
		}
		
		Patient patient = patientRepository.findBypatientId(patUpdate.getPatient_id()).get();
		if(patient==null) {
			throw new UserNotFoundException("Patient Not Found with Id :"+patUpdate.getPatient_id());
		}
		User user = userService.getUser(patUpdate.getPatient_id());
		if(patUpdate.getName()!=null) {
			patient.setName(patUpdate.getName());
		}
		if(patUpdate.getGender()!=null) {
			patient.setGender(patUpdate.getGender());
		}
		if(patUpdate.getAge() != null) {
			patient.setAge(patUpdate.getAge());
		}
		if(patUpdate.getAddress() != null) {
			patient.setAddress(patUpdate.getAddress());
		}
		if(patUpdate.getEmail()!=null) {
			if(userService.emailExist(patUpdate.getEmail()) && !user.getEmail().equals(patUpdate.getEmail())) {
				throw new EmailAlreadyExistsException("Email "+patUpdate.getEmail() +" given for Update Already Exist");
			}
			user.setEmail(patUpdate.getEmail());
		}
		if(patUpdate.getPhoneNumber()!=null) {
			if(userService.phoneNumberExist(patUpdate.getPhoneNumber()) && !user.getPhoneNumber().equals(patUpdate.getPhoneNumber())) {
				throw new PhoneNumberAlreadyExistsException("Phone Number "+ patUpdate.getPhoneNumber() +" given for Update Already Exist");
			}
			user.setPhoneNumber(patUpdate.getPhoneNumber());
		}
		if(patUpdate.getPassword()!=null) {
			user.setPassword(passwordEncoder.encode(patUpdate.getPassword()));
		}
		patientRepository.save(patient);
		userService.updateUser(user);
	}

}
