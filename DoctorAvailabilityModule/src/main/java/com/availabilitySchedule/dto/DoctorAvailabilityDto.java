package com.availabilitySchedule.dto;

import com.availabilitySchedule.model.Specialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Doctor.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDto {
	public DoctorAvailabilityDto(String string, String string2, Specialization cardiology) {
		// TODO Auto-generated constructor stub
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
	private String doctorId;
	private String doctorName;
	private Specialization specialization;
}