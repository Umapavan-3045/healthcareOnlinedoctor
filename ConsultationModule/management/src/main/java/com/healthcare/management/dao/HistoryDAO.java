package com.healthcare.management.dao;

import java.util.List;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.healthcare.management.entity.MedicalHistory;

/**
 * HistoryDAO is a Data Access Object (DAO) interface for managing medical history records.
 * It extends JpaRepository to provide CRUD operations and custom query methods for MedicalHistory entities.
 * 
 * @JpaRepository - Indicates that this interface is a JPA repository.
 */

public interface HistoryDAO extends JpaRepository<MedicalHistory, String> {
    
   
    
    /**
     * Retrieves medical history records filtered by patient ID.
     * 
     * @param patientId - The patient ID to filter medical history records.
     * @return MedicalHistory - The medical history record filtered by patient ID.
     */
	@Query("SELECT h FROM MedicalHistory h WHERE h.patientId = :patientId")
    public List<MedicalHistory> getMedicalHistoryByPatientId(@Param("patientId") String patientId);
}