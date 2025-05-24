package com.users.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.users.app.dto.DoctorAvailabilityDto;
import com.users.app.dto.DoctorDto;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.DoctorNotFoundException;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.model.Doctor;
import com.users.app.model.User;
import com.users.app.repository.DoctorRepository;

@Service
public class DoctorService {
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private UserService userService;
	
	@Lazy
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public DoctorDto getDoctorById(String Id) {
		Optional<Doctor> doc = doctorRepository.findBydoctorId(Id);
		if(!doc.isPresent()) {
			throw new DoctorNotFoundException("doctor not found with Id :"+ Id);
		}
		Optional<User> user = userService.getUserById(Id);
		DoctorDto docDetails = new DoctorDto();
		
		docDetails.setName(doc.get().getName());
		docDetails.setDoctor_id(doc.get().getDoctorId());
		docDetails.setSpecialization(doc.get().getSpecialization());
		docDetails.setEmail(user.get().getEmail());
		docDetails.setPhoneNumber(user.get().getPhoneNumber());
		//docDetails.setPassword(user.get().getPassword());
		
		return docDetails;
		
	}
	public List<Doctor> getDoctorBySpecialization(Specialization specialization){
		
		List<Doctor> doctors = doctorRepository.findDoctorBySpecialization(specialization);
		
		if(doctors.isEmpty()) {
			throw new DoctorNotFoundException("No Doctor Found");
		}
		return doctors;
	}
	public void updateDoctorDetails(DoctorDto docUpdate) {
		
		Optional<Doctor> doc = doctorRepository.findBydoctorId(docUpdate.getDoctor_id());
		if(!doc.isPresent()) {
			throw new DoctorNotFoundException("Doctor not found with this Id: "+docUpdate.getDoctor_id());
		}
		Doctor doctor = doctorRepository.findBydoctorId(docUpdate.getDoctor_id()).get();
		User user = userService.getUser(docUpdate.getDoctor_id());
		if(docUpdate.getName() != null) {
			doctor.setName(docUpdate.getName());
		}
		if(docUpdate.getSpecialization()!=null) {
			boolean isValidSpecialization = Arrays.stream(Specialization.values())
                    .anyMatch(s -> s.name().equalsIgnoreCase(docUpdate.getSpecialization().toString()));

			if (!isValidSpecialization) {
			throw new IllegalArgumentException("Invalid Specialization: " + docUpdate.getSpecialization());
			}
			
			doctor.setSpecialization(docUpdate.getSpecialization());
		}
		if(docUpdate.getEmail()!=null) {
			if(userService.emailExist(docUpdate.getEmail()) && !user.getEmail().equals(docUpdate.getEmail())) {
				throw new EmailAlreadyExistsException("Email "+docUpdate.getEmail() +" given for Update Already Exist");
			}
			user.setEmail(docUpdate.getEmail());
		}
		if(docUpdate.getPhoneNumber()!=null) {
			if(userService.phoneNumberExist(docUpdate.getPhoneNumber()) && !user.getPhoneNumber().equals(docUpdate.getPhoneNumber())) {
				throw new PhoneNumberAlreadyExistsException("Phone Number "+ docUpdate.getPhoneNumber() +" given for Update Already Exist");
			}
			user.setPhoneNumber(docUpdate.getPhoneNumber());
		}
		if(docUpdate.getPassword()!=null) {
			
			user.setPassword(passwordEncoder.encode(docUpdate.getPassword()));
		}
		doctorRepository.save(doctor);
		userService.updateUser(user);
	}

	public List<Doctor> getAllDoctorsdetails() {
		
		return doctorRepository.findAll();
	}
	
	public List<DoctorAvailabilityDto> getAllDoctors() {
		
		List<Doctor> doctors = doctorRepository.findAll();
		List<DoctorAvailabilityDto> doctorDTOs = new ArrayList<>();
        
        for (Doctor doctor : doctors) {
            doctorDTOs.add(DoctorAvailabilityDto.fromEntity(doctor));
        }
        
        return doctorDTOs;	
    }
}
