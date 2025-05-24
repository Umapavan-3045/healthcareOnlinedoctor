package com.healthcare.management.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultationDto {
	
	@NotNull(message="Appointment ID cannot be null")
	private String appointmentId;

}
