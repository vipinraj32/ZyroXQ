package xyz.zyro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.service.AdvertiserService;

@RestController
@RequestMapping("/zyro")
public class AllDetailsController {

	
	@Autowired
	private AdvertiserService service;
	
	@GetMapping("/get-Details")
	public ResponseEntity<AdvertiserDTO> getProfileDetails() {
		
		return ResponseEntity.ok(service.getProfileDetails("vipendrarajpoot2@gmail.com"));
				
	}
	
	
}
