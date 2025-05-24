package com.dataBase.automate.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import com.dataBase.automate.model.Status;
import com.dataBase.automate.model.TimeSlots;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class AppointmentDto {
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
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
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
	@NotEmpty
	private String appointmentId;
	@NotNull 
    private TimeSlots timeSlot;
	@NotNull
    private Status status;
	@NotEmpty
    private String doctorId;
    private LocalDate date;
    @NotEmpty
    private String availabilityId;
    @NotEmpty
    private String patientId;
    @NotEmpty
    private String patientName;
    @NotEmpty
    private String doctorName;
   
	
}