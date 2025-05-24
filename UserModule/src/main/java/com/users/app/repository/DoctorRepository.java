package com.users.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.users.app.enums.Specialization;
import com.users.app.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
	
	@Query("select doc from doctor doc where doc.doctorId = :doctorId")
	Optional<Doctor> findBydoctorId(@Param("doctorId")String doctorId);

	@Query("select doctors from doctor doctors where doctors.specialization = :specialization")
	List<Doctor> findDoctorBySpecialization(@Param("specialization")Specialization specialization);
	 
}
