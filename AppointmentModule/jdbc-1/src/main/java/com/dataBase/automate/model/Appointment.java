package com.dataBase.automate.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

/**
* Appointment Table.
* 
* @Author Sanjay R
* @Since 2025-03-18
*/

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    public String getAppointmentId() {
		return appointmentId;
	}


	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}


	public TimeSlots getTimeSlot() {
		return timeSlot;
	}


	public void setTimeSlot(TimeSlots timeSlot) {
		this.timeSlot = timeSlot;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public String getPatientName() {
		return patientName;
	}


	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}


	public String getDoctorName() {
		return doctorName;
	}


	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}


	public String getPatientId() {
		return patientId;
	}


	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}


	public String getDoctorId() {
		return doctorId;
	}


	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public String getAvailabilityId() {
		return availabilityId;
	}


	public void setAvailabilityId(String availabilityId) {
		this.availabilityId = availabilityId;
	}


	@Id
    
    @Column(name="appointment_id", nullable = false, unique = true)
    private String appointmentId;
    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot")
    private TimeSlots timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
    
    @Column(name="patientname", nullable = true)
    private String patientName;
    
    @Column(name="doctorname", nullable = true)
    private String doctorName;
    
    @Column(name = "patient_id",nullable = false)
    private String patientId;

    @Column(name = "doctor_id" , nullable = false)
    private String doctorId;
    
    
    @Column(name = "date",nullable = false)
    private LocalDate date;
    
    @Column(name = "availability_id",nullable = false,unique=true)
    private String availabilityId;
	

	@PrePersist
	protected void onCreate() {
	    if (appointmentId == null) {
	        appointmentId = UUID.randomUUID().toString();
	    }
	}
}