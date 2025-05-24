package com.availabilitySchedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for the Availability Schedule application.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 * 
 *        This class is responsible for bootstrapping the Spring Boot
 *        application.
 */
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AvailabilityScheduleApplication {

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AvailabilityScheduleApplication.class, args);
	}
}