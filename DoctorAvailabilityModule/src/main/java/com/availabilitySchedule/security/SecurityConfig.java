package com.availabilitySchedule.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import feign.RequestInterceptor;
//import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.csrf(customizer -> customizer.disable()).authorizeHttpRequests(request -> request
//                        .requestMatchers("")
//                        .permitAll()
				
				.anyRequest().permitAll())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();

	}

//    @Bean
//	RequestInterceptor requestInterceptor() {
//		return requestTemplate -> {
//			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//			if(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
//				HttpServletRequest request = servletRequestAttributes.getRequest();
//				String authHeader = request.getHeader("AUTHORIZATION");
//				System.out.print(">>>>>>>>>>>>" + authHeader);
//				if(authHeader != null && !authHeader.isEmpty()) {
//					requestTemplate.header("AUTHORIZATION", authHeader);
//				}
//			}
//		};
//	}
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
//        provider.setUserDetailsService(userDetailsService);
//
//
//        return provider;
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//
//    }
}
