package xyz.zyro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import xyz.zyro.entity.User;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.UserRepository;


@Service
public class CustomUserDetails implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("Invalid Email/Password"));
		return user;
	}

	

}
