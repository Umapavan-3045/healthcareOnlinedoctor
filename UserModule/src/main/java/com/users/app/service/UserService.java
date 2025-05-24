package com.users.app.service;

import java.util.Optional;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.users.app.dto.PasswordGenerator;
import com.users.app.dto.UpdatePasswordDto;
import com.users.app.dto.UserDto;
import com.users.app.enums.Gender;
import com.users.app.enums.Role;
import com.users.app.enums.Specialization;
import com.users.app.exceptions.EmailAlreadyExistsException;
import com.users.app.exceptions.PhoneNumberAlreadyExistsException;
import com.users.app.exceptions.UserNotFoundException;
import com.users.app.feignClient.AvailabilityFeignClient;
import com.users.app.model.Doctor;
import com.users.app.model.Patient;
import com.users.app.model.User;
import com.users.app.model.UserPrincipal;
import com.users.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private JWTService jwtService;
    
    @Lazy
	@Autowired
	private AuthenticationManager authManager;
 
	@Lazy
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AvailabilityFeignClient availability;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PasswordGenerator generate;
	
    @Transactional
    public void signUp(UserDto userDto) {
        User user = new User();
        user.setPhoneNumber(userDto.getPhoneNumber());
        
        if(userDto.getRole().toString().equals("ADMIN")){
        	user.setEmail(userDto.getEmail());
        	user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        	user.setPhoneNumber(userDto.getPhoneNumber());
        	user.setRole(userDto.getRole());
        	userRepository.save(user);
        	return ;
        }
        
        if(!userDto.getRole().equals(Role.DOCTOR) && !userDto.getRole().equals(Role.PATIENT)) {
        	throw new IllegalArgumentException("Role Does not Exists");
        }
        if(userDto.getRole().equals(Role.DOCTOR) && userDto.getDoctor() == null){
        	throw new IllegalArgumentException("Please Provide Doctor Details");
        }
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());
        
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        if (userDto.getRole().equals(Role.DOCTOR) && userDto.getDoctor() != null) {
        		if(userDto.getDoctor().getName() == null) {
        			throw new IllegalArgumentException("Doctor name Should not be NULL");
        		}
        		if(userDto.getDoctor().getSpecialization() == null) {
        			throw new IllegalArgumentException("Doctor Specialization Should not be NULL");
        		}
        		boolean isValidSpecialization = Arrays.stream(Specialization.values())
                        .anyMatch(s -> s.name().equalsIgnoreCase(userDto.getDoctor().getSpecialization().toString()));

				if (!isValidSpecialization) {
				throw new IllegalArgumentException("Invalid Specialization: " + userDto.getDoctor().getSpecialization());
				}
            user.setDoctor(userDto.getDoctor());
        }
        if(userDto.getRole().equals(Role.PATIENT) && userDto.getPatient() == null){
        	throw new IllegalArgumentException("Please Provide Patient Details");
        }
        if (userDto.getRole().equals(Role.PATIENT) && userDto.getPatient() != null) {
        	
        	if(userDto.getPatient().getName() ==null)
        	{
        		throw new IllegalArgumentException("Patient Name Should not be NULL");
        	}
        	if(userDto.getPatient().getAge() ==null) {
        		throw new IllegalArgumentException("Patient Age Should not be NULL");
        	}
        	if(userDto.getPatient().getGender() ==null) {
        		throw new IllegalArgumentException("Patient Gender Should not be NULL");
        	}
        	if(!userDto.getPatient().getGender().equals(Gender.MALE) && !userDto.getPatient().getGender().equals(Gender.FEMALE)) {
            	throw new IllegalArgumentException("Gender Does not Exists");
            }
            user.setPatient(userDto.getPatient());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + user.getEmail() + " already exists");
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new PhoneNumberAlreadyExistsException("Phone number " + user.getPhoneNumber() + " already exists");
        }
        if (user.getRole().equals(Role.DOCTOR)) {
            Doctor doctor = user.getDoctor();
            doctor.setUserDoctor(user);
            user.setDoctor(doctor);
            userRepository.save(user);
            availability.createAvailabilityForDoctorId(user.getId(),user.getDoctor().getName(), user.getDoctor().getSpecialization());
            
        } else if (user.getRole().equals(Role.PATIENT)) {
            Patient patient = user.getPatient();
            patient.setUserPatient(user);
            user.setPatient(patient);
            userRepository.save(user);
        }
    }

    public boolean emailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean phoneNumberExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

//    public Optional<User> signIn(String identifier, String password) {
//        Optional<User> user = userRepository.findByEmail(identifier);
//        if (!user.isPresent()) {
//            user = userRepository.findByPhoneNumber(identifier);
//        }
//        if (user.isEmpty()) {
//            throw new UserNotFoundException("No user found with " + identifier);
//        }
//        if (!user.get().getPassword().equals(password)) {
//            throw new PasswordMismatchException("Incorrect password");
//        }
//        return user;
//    }
    
    public String verify(String email, String password, Role role) {
	    Authentication authentication = authManager
	            .authenticate(new UsernamePasswordAuthenticationToken(email, password));
	    
	    if (!authentication.isAuthenticated()) {
	        throw new UserNotFoundException("Invalid Login Credentials!");
	    }
	    User authUser = userRepository.findByEmail(email).get();
 
	    if (authUser.getRole() != role) {
	        throw new UserNotFoundException("Forbidden! You don't have access!");
	    }
	    
	    return jwtService.getToken(authUser.getId(), role, email);
	}

    public Optional<User> getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        return user;
    }

    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        availability.deleteAllAvailability(id);
        userRepository.deleteById(id);
    }

    @Override
	public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(useremail);
 
		if(user == null) {
			throw new UsernameNotFoundException("User Not Found....");
		}
		
		return new UserPrincipal(user.get());
	}

	public void updatePassword(String id,UpdatePasswordDto passwordDto) throws Exception {
		Optional<User> user = userRepository.findById(id);
		if(user.isPresent()) {
			if(passwordEncoder.matches(passwordDto.getOldPassword(), user.get().getPassword())){
				User updatedUser = user.get();
				updatedUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
				userRepository.save(updatedUser);
			}
			else {
				throw new Exception("passwords doesn't match");
			}
		}
		
	}
	
	@Async
	public void sendForgotPasswordEmail(String toMail, String websiteLink) {
		
		Optional<User> user = userRepository.findByEmail(toMail);
		
		if(!user.isPresent()) {
			throw new UserNotFoundException("No User found with this Mail Id");
		}
		String username = user.get().getRole().equals(Role.PATIENT) ? user.get().getPatient().getName() : user.get().getDoctor().getName() ;
		
		
	    SimpleMailMessage message = new SimpleMailMessage();
	    
	    
	    String newGeneratedPassword = generate.generateRandomPassword(8);
	    
	    User updatedUser = user.get();
		updatedUser.setPassword(passwordEncoder.encode(newGeneratedPassword));
		userRepository.save(updatedUser);
		System.out.println("new Password is "+newGeneratedPassword);
	    message.setFrom("healthcareonlinedoctor@gmail.com");
	    message.setTo(toMail);
	    message.setSubject("Your New Temporary Password for Health Care Online Doctor - Password Reset");

	    String emailBody = String.format(
	            "Dear %s,\n\n" +
	            "You recently requested to reset your password for your Health Care Online Doctor account.\n\n" +
	            "As per your request, we have generated a new temporary password for you:\n\n" +
	            "Temporary Password: %s\n\n" +
	            "Please use this temporary password to log in to your account on our website:\n\n" +
	            "%s\n\n" +
	            "Once you have successfully logged in, **we strongly recommend that you change your password immediately** to something more secure and memorable.\n\n" +
	            "If you did not request a password reset, please disregard this email. Your account security is important to us.\n\n" +
	            "Thank you for using Health Care Online Doctor.\n\n" +
	            "Sincerely,\n\n" +
	            "The Health Care Online Doctor Team",
	            username, newGeneratedPassword, websiteLink
	    );

	    message.setText(emailBody);
	    mailSender.send(message);
	}
	
}