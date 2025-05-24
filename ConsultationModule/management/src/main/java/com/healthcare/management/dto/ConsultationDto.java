package com.healthcare.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConsultationDto is a Data Transfer Object (DTO) that represents the details of a consultation.
 * It includes fields for consultation ID, appointment ID, notes, and prescription.
 * 
 * @Data - Generates getters, setters, toString, equals, and hashCode methods.
 * @NoArgsConstructor - Generates a no-argument constructor.
 * @AllArgsConstructor - Generates an all-argument constructor.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDto {
    public ConsultationDto(String consultationId2, String appointmentId2, String notes2, String prescription2) {
		// TODO Auto-generated constructor stub
	}

	public ConsultationDto() {
		// TODO Auto-generated constructor stub
	}

	public String getConsultationId() {
		return consultationId;
	}

	public void setConsultationId(String consultationId) {
		this.consultationId = consultationId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}

	private String consultationId;
    
    @NotNull(message="Appointment ID cannot be null")
    private String appointmentId; 
    
    @Size(max = 500 , message="Notes cannot exceed more than 500 characters")
    @NotBlank(message="Notes cannot be left blank")
    @NotNull(message="Notes cannot be null")
    private String notes;
    
    @Size(max=1000,message="Prescription cannot exceed 1000 characters")
    @NotNull(message="Prescription cannot be null")
    @NotBlank(message="Prescription cannot be left blank")
    private String prescription;
    
    
    
}