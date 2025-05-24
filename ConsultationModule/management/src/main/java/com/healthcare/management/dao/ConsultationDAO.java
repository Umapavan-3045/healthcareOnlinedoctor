package com.healthcare.management.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.healthcare.management.entity.Consultation;


/**
 * ConsultationDAO is a Data Access Object (DAO) interface for managing consultation records.
 * It extends JpaRepository to provide CRUD operations and custom query methods for Consultation entities.
 * 
 * @JpaRepository - Indicates that this interface is a JPA repository.
 */

public interface ConsultationDAO extends JpaRepository<Consultation, String> {
	
	/**
	 * Retrieves consultation records filtered by appointment ID.
	 * 
	 * @param appointmentId - The appointment ID to filter consultation records.
	 * @return List<Consultation> - A list of consultation records filtered by appointment ID.
	 */	
	
	@Query("SELECT c from Consultation c where c.appointmentId=:appointmentId")
	public List<Consultation> findConsultationDetailsByAppointmentId(@Param("appointmentId") String appointment_Id);
	
	Optional<Consultation> findConsultationByAppointmentId(String appointmentId);

	/**
	 * Retrieves a consultation record by consultation ID.
	 * 
	 * @param consultationId - The consultation ID to filter consultation records.
	 * @return Optional<Consultation> - An optional containing the consultation record if found, or empty if not found.
	 */
	Optional<Consultation>findByConsultationId(String consultationId);
	
	 boolean existsByAppointmentId(String appointmentId);
	
}	
