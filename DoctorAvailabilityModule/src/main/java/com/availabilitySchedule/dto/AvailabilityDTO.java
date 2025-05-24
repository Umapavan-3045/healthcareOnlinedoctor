package com.availabilitySchedule.dto;

import com.availabilitySchedule.model.Availability;
import com.availabilitySchedule.model.Specialization;
import com.availabilitySchedule.model.Status;
import com.availabilitySchedule.model.Timeslots;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for Availability. Converts between Availability entity
 * and DTO.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDTO {
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

	private String availabilityId;
	private String doctorId;
	private String doctorName;
	private Specialization specialization;
	private LocalDate date;
	private Timeslots timeSlots;

	public AvailabilityDTO(String availabilityId, String doctorId, String doctorName,
            Specialization specialization, LocalDate date, Timeslots timeSlots) {
			this.availabilityId = availabilityId;
			this.doctorId = doctorId;
			this.doctorName = doctorName;
			this.specialization = specialization;
			this.date = date;
			this.timeSlots = timeSlots;
	}

	public AvailabilityDTO() {
		// TODO Auto-generated constructor stub
	}

	public Availability toEntity() {
		Availability availability = new Availability();
		availability.setAvailabilityId(availabilityId);
		availability.setDoctorId(this.doctorId);
		availability.setDoctorName(this.doctorName);
		availability.setSpecialization(this.specialization);
		availability.setDate(this.date);
		availability.setTimeSlots(this.timeSlots);
		availability.setStatus(Status.Available);

		return availability;
	}

	public static AvailabilityDTO fromEntity(Availability availability) {
		AvailabilityDTO dto = new AvailabilityDTO();
		dto.setAvailabilityId(availability.getAvailabilityId());
		dto.setDoctorId(availability.getDoctorId());
		dto.setDoctorName(availability.getDoctorName());
		dto.setSpecialization(availability.getSpecialization());
		dto.setDate(availability.getDate());
		dto.setTimeSlots(availability.getTimeSlots());
		return dto;
	}
}