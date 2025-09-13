package xyz.zyro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import xyz.zyro.dto.LoginRequest;
import xyz.zyro.dto.Response;
import xyz.zyro.entity.User;
import xyz.zyro.repository.UserRepository;
import xyz.zyro.security.AuthenticateUtill;

@Service
@Slf4j
public class AuthenticatService {
	@Autowired	
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;
	
	private AuthenticateUtill authenticateUtill;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	private Response login(LoginRequest request) {
	     Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
	        );
	     
	     User user = (User) authentication.getPrincipal();

	        String token = authenticateUtill.generateAccessToken(user);

	        return new Response(token, user.getEmail(), user.getAuthorities().toString(), user.getName());
	    }
	}
	

}
