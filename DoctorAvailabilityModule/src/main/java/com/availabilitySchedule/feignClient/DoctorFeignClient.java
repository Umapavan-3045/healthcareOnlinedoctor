/**
 * Interface for communicating with user module to fetch doctor data
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
package com.availabilitySchedule.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.availabilitySchedule.dto.DoctorAvailabilityDto;

@FeignClient("USER-SERVICE")
public interface DoctorFeignClient {
	@GetMapping("/api/doctor/alldoctors")
    public List<DoctorAvailabilityDto> getAllDoctors();
}
