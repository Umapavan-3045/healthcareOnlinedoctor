package com.healthcare.management.dao;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//import com.healthcare.management.entity.Consultation;
import com.healthcare.management.entity.Patient;

public interface PatientDAO extends JpaRepository<Patient, String>{
	
}
