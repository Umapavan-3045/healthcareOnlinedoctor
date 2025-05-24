package com.healthcare.management.entity;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {
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

		@Id
		//@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name="Consultation_id" ,nullable = false)
		private String consultationId; 
		
		private String appointmentId;
		
		@Column(name = "Notes")
		@Size(max = 500)
		private String notes; 
		
		@Column(name = "Prescription" , nullable = false)
		@NotNull
		private String prescription;
		
		@PrePersist
	    public void generateUUID() {
	        if (this.consultationId == null || this.consultationId.isEmpty()) {
	            this.consultationId = UUID.randomUUID().toString();
	        }
		}

}
