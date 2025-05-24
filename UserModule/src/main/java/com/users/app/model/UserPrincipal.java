package com.users.app.model;

import java.util.Collection;
import java.util.Collections;
 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
public class UserPrincipal implements UserDetails {
	private User user;
	public UserPrincipal(User user) {
		this.user = user;
	}
 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
	}
 
	@Override
	public String getPassword() {
		return user.getPassword();
	}
 
	@Override
	public String getUsername() {
		return user.getEmail();
	}
 
	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}
 
	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}
 
	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}
 
	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
}
