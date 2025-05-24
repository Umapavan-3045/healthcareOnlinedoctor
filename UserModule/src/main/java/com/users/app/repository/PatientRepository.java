package com.users.app.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.users.app.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, String> {
			
		@Query("select pat from Patient pat where pat.patientId = :patientId ")
		public Optional<Patient> findBypatientId(@Param("patientId") String patientId);
}
