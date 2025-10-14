package xyz.zyro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xyz.zyro.dto.InfluncerDTO;
import xyz.zyro.dto.InstagramMediaResponseDTO;
import xyz.zyro.dto.ProfileDetailsDTO;
import xyz.zyro.entity.Influncer;
import xyz.zyro.service.InfluncerService;

@RestController
@RequestMapping("/influncer")
public class InfluncerController {
	
	@Autowired
	private InfluncerService service;
	
	@PostMapping("/update-details")
	public ResponseEntity<InfluncerDTO> updateDetails(@RequestBody @Valid Influncer influncer){
		return ResponseEntity.ok(service.updateInfluncerDetails(influncer));
	}
	
	@GetMapping("/profile-details")
	public ResponseEntity<ProfileDetailsDTO> getProfileDetails(@RequestParam("username")String username){
		return ResponseEntity.ok(service.getProfileDetails(username));
	}
	
	@GetMapping("/media")
	public ResponseEntity<InstagramMediaResponseDTO> getMedia(@RequestParam("username")String username){
		return ResponseEntity.ok(service.getMediaResponse(username));
	}
}
