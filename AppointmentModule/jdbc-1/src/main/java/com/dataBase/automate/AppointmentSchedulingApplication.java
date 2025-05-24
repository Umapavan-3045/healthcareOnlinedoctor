package com.dataBase.automate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Service for managing Appointments.
 * 
 * @Author Sanjay R
 * @Since 2025-03-18
 */
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class AppointmentSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentSchedulingApplication.class, args);
	}

}
