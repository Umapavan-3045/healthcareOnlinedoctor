package com.availabilitySchedule.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import com.availabilitySchedule.model.Availability;
import java.util.List;
import com.availabilitySchedule.model.Specialization;

/**
 * Repository interface for Availability entities.
 * 
 * Provides methods to perform CRUD operations and custom queries on
 * Availability entities.
 * 
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.availabilitySchedule.model.Availability
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, String> {

	/**
	 * Finds a list of availabilities by doctor ID and date.
	 * 
	 * @param doctorId the ID of the doctor
	 * @param date     the date of the availability
	 * @return a list of availabilities for the specified doctor and date
	 */
	List<Availability> findByDoctorIdAndDate(String doctorId, LocalDate date);

	/**
	 * Finds a list of availabilities by doctor ID.
	 * 
	 * @param doctorId the ID of the doctor
	 * @return the list of availabilities for the specified doctor
	 */
	List<Availability> findByDoctorId(String doctorId);
	
	
	@Transactional
	void deleteByDoctorId(String doctorId);
	
	/**
	 * Finds a list of availabilities by specialization and date.
	 * 
	 * @param specialization the specialization of the doctor
	 * @param date           the date of the availability
	 * @return a list of availabilities for the specified specialization and date
	 */
	List<Availability> findBySpecializationAndDate(Specialization specialization, LocalDate date);

	/**
	 * Finds a list of availabilities with dates before the specified date.
	 * 
	 * @param date the date to compare
	 * @return a list of availabilities with dates before the specified date
	 */
	List<Availability> findByDateBefore(LocalDate date);

	/**
	 * Finds a list of availabilities by doctor ID and date range.
	 * 
	 * @param doctorId  the ID of the doctor
	 * @param startDate the start date of the range
	 * @param endDate   the end date of the range
	 * @return a list of availabilities for the specified doctor and date range
	 */
	List<Availability> findByDoctorIdAndDateBetween(String doctorId, LocalDate startDate, LocalDate endDate);

	/**
	 * Finds a list of availabilities by specialization and date range.
	 * 
	 * @param specialization the specialization of the doctor
	 * @param startDate      the start date of the range
	 * @param endDate        the end date of the range
	 * @return a list of availabilities for the specified specialization and date
	 *         range
	 */
	List<Availability> findBySpecializationAndDateBetween(Specialization specialization, LocalDate startDate,
			LocalDate endDate);

	boolean existsByDateBetween(LocalDate nextMonday, LocalDate plusDays);
}