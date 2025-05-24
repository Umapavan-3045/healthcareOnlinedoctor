package com.dataBase.automate.feignClients;
import java.time.LocalDate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.dataBase.automate.dto.AvailabilityDto;
import com.dataBase.automate.model.Specialization;
import java.util.List;
/**
* Availability Service Feign Client.
* 
* @Author Sanjay R
* @Since 2025-03-18
*/
@FeignClient(name="AVAILABILITY-SERVICE",configuration= FeignClientConfiguration.class)
public interface AvailabilityFeignClient {
	
	@PutMapping("/availability/update/{availableId}/reschedule/{unavailableId}")//done//works
	public ResponseEntity<String> updateAvailability(@PathVariable String availableId,@PathVariable String unavailableId);
	
	@GetMapping("/availability/doctor/{doctorId}/date/{date}")//done//works
	public List<AvailabilityDto> getAvailabilityByDoctorIdAndDate(@PathVariable String doctorId,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
	
	@GetMapping("/availability/specialization/{specialization}/date/{date}")//done//works
	public List<AvailabilityDto> getAvailabilityBySpecializationAndDate(@PathVariable Specialization specialization, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
	
	@PutMapping("/availability/book/{availabilityId}")//done//works
	public ResponseEntity<AvailabilityDto> bookTimeSlot(@PathVariable String availabilityId);
	
	@PutMapping("/availability/cancel/{availableId}")//works
	public ResponseEntity<String> cancelAvailability(@PathVariable String availableId) ;
	
	@GetMapping("/availability/AvailabilityId/{availabilityId}")//done//works
	 public ResponseEntity<AvailabilityDto> viewById(@PathVariable String availabilityId);
	 
	@GetMapping("/availability/doctor/{doctorId}/date-range")//done//works
    public List<AvailabilityDto> getAvailabilityByDoctorIdAndDateRange(@PathVariable String doctorId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
	
	@GetMapping("/availability/specialization/{specialization}/date-range")//done//works
    public List<AvailabilityDto> getAvailabilityBySpecializationAndDateRange(@PathVariable Specialization specialization,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}
