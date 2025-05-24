 package com.dataBase.automate.feignClients;
 
 import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dataBase.automate.dto.AppointmentDto;

import com.dataBase.automate.dto.Response;
import com.dataBase.automate.model.Appointment;
/**
* Notification Service Feign Client.
* 
* @Author Sanjay R
* @Since 2025-03-18
*/
 @FeignClient(name="NOTIFICATION-SERVICE",configuration= FeignClientConfiguration.class)
public interface NotificationFeignClient {
	 
	 
	 
	 @PostMapping("/notifications/create")
	  public ResponseEntity<String> createNotification(@RequestBody AppointmentDto appointmentDto);
	 
	 @PutMapping("/notifications/onCompletion")
	  public void onCompletion(@RequestBody AppointmentDto appointment);
	 

	 @PutMapping("/notifications/onUpdate")
	 public void onUpdate(@RequestBody Appointment appointment);
	 
	 
	 
}