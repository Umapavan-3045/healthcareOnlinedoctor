package com.cts.healthcareappointment.notificationmodule.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.cts.healthcareappointment.notificationmodule.dto.Appointmentdto;

@FeignClient(name="APPOINTMENT-SERVICE")
public interface Appointmentclient {
	
	 @PutMapping("/appointments/notifyCompletion/{appointmentId}")
	    public Appointmentdto notifyCompletion(@PathVariable String appointmentId);
	 
	 
	 
	
	

}