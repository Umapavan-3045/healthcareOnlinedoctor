package com.users.app.repository;


import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.users.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	 @Query("select u from User u where u.email = :email")
     Optional<User> findByEmail(@Param("email") String email);
	 
	    
	 @Query("select u from User u where u.phoneNumber = :phoneNumber")
	 Optional<User> findByPhoneNumber(@Param("phoneNumber")String phoneNumber);
	 
	 @Query("select u from User u where u.id = :id")
	 Optional<User> findById(@Param("id")String id);
	 
	 @Query("delete from User u where u.id = :id")
	 void deleteById(@Param("id")String id);
	 
}
