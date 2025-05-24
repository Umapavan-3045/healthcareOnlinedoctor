package com.users.app.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.users.app.enums.Specialization;


@FeignClient(name="AVAILABILITY-SERVICE",configuration= FeignClientConfiguration.class)
public interface AvailabilityFeignClient {
	
	@PostMapping("/availability/create/doctor/{doctorId}/{doctorName}/{specialization}")
	public void createAvailabilityForDoctorId(@PathVariable String doctorId,
			@PathVariable String doctorName, @PathVariable Specialization specialization);
	
	@DeleteMapping("/availability/deletebydoctorId/{doctorId}")
	public void deleteAllAvailability(@PathVariable String doctorId);

}
