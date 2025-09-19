package xyz.zyro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.entity.Advertiser;
import xyz.zyro.service.AdvertiserService;
@RestController
@RequestMapping("/advestiser")
public class AdvertiserController {
	
	@Autowired
	private AdvertiserService service;
	
	@PostMapping("/update-details")
	public ResponseEntity<AdvertiserDTO> updateAdvertiser(@RequestBody @Valid  Advertiser advertiser){ 
		return ResponseEntity.ok(service.updateAdvertiserDetails(advertiser));
	}
	
	@GetMapping("/update")
	public ResponseEntity<String> show(){
		return ResponseEntity.ok(service.show());
	}
}
