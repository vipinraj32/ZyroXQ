package xyz.zyro.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.zyro.dto.LoginRequest;
import xyz.zyro.dto.Response;
import xyz.zyro.entity.User;
import xyz.zyro.service.AuthenticatService;

@RestController
@RequestMapping("/user")
public class AuthenticateController {

	@Autowired
	private AuthenticatService authenticatService;
	
	@PostMapping("/advertiser/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequestDto) {
        return ResponseEntity.ok(authenticatService.login(loginRequestDto));
    }
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody User user){
		User u=authenticatService.signup(user);
		Map<String,Object>response= new HashMap<>();
		response.put("message","Successfully Register");
		response.put("status",HttpStatus.CREATED.value());
	    return ResponseEntity.ok(response);	
	} 
	

}
