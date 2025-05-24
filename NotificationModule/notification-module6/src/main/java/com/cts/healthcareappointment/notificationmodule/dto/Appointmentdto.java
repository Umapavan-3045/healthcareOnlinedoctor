package com.cts.healthcareappointment.notificationmodule.dto;

import java.time.LocalDate;

import com.cts.healthcareappointment.notificationmodule.Entity.Status;
import com.cts.healthcareappointment.notificationmodule.Entity.TimeSlots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointmentdto {


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
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
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
	private String appointmentId;
    private TimeSlots timeSlot;
    private Status status;
    private String doctorId;
    private String doctorName;
    private String patientName;
    private String patientId;
    private LocalDate date;
    private String availabilityId;
    
}