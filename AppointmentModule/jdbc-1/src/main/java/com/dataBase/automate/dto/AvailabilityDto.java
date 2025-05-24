package com.dataBase.automate.dto;

import com.dataBase.automate.model.Specialization;
import com.dataBase.automate.model.TimeSlots;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class AvailabilityDto {
	public String getAvailabilityId() {
		return availabilityId;
	}
	public void setAvailabilityId(String availabilityId) {
		this.availabilityId = availabilityId;
	}
	public TimeSlots getTimeSlots() {
		return timeSlots;
	}
	public void setTimeSlots(TimeSlots timeSlots) {
		this.timeSlots = timeSlots;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public Specialization getSpecialization() {
		return specialization;
	}
	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}
	@NotEmpty
    private String availabilityId;
	@NotNull
    private TimeSlots timeSlots;
	@NotNull
    private LocalDate date;
	@NotEmpty
    private String doctorId;
	@NotNull
    private Specialization specialization;
    
}
