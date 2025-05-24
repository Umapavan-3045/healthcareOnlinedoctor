package com.availabilitySchedule.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing availability.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Availability {

	public Availability(String bookedId, String string, String string2, Specialization cardiology, LocalDate today,
			Timeslots nineToEleven, Status unavailable) {
		// TODO Auto-generated constructor stub
	}

	public Availability() {
		// TODO Auto-generated constructor stub
	}

	public String getAvailabilityId() {
		return availabilityId;
	}

	public void setAvailabilityId(String availabilityId) {
		this.availabilityId = availabilityId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Timeslots getTimeSlots() {
		return timeSlots;
	}

	public void setTimeSlots(Timeslots timeSlots) {
		this.timeSlots = timeSlots;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Id
	@Column(name = "availability_id", nullable = false, unique = true)
	private String availabilityId;

	@Column(name = "doctor_id", nullable = true)
	private String doctorId;
	
	@Column(name = "doctor_name", nullable = true)
	private String doctorName;

	@Enumerated(EnumType.STRING)
	@Column(name = "specialization", nullable = true)
	private Specialization specialization;

	@Column(name = "date", nullable = true)
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "slot")
	private Timeslots timeSlots;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@PrePersist
	protected void onCreate() {
		if (availabilityId == null) {
			availabilityId = UUID.randomUUID().toString();
		}
	}
}