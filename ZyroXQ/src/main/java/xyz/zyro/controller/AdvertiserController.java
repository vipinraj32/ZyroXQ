package xyz.zyro.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.entity.Advertiser;
import xyz.zyro.exception.CustomIOException;
import xyz.zyro.service.AdvertiserService;
@RestController
@RequestMapping("/advestiser")
public class AdvertiserController {
	
	@Autowired
	private AdvertiserService service;
	
	@PostMapping("/update-details")
	public ResponseEntity<AdvertiserDTO> updateAdvertiser(@Valid @RequestParam("companyName")String companyName, @RequestParam("name")String name, @RequestParam("walletAdress")String walletAddess, @RequestParam("stackAmount")Double stackAmount, @RequestParam("mobile")String mobile, @RequestParam("file")MultipartFile file , @RequestParam("email")String email){
		byte[] filedata;
		try {
		filedata=file.getBytes();
		}catch (IOException e) {
			throw new CustomIOException("Failed To update Profile details:"+e.getMessage());
		}
		Advertiser advertiser=Advertiser.builder()
				.companyName(companyName)
				.name(name)
				.walletAddres(walletAddess)
				.stakeAmount(stackAmount)
				.mobile(mobile)
				.imageData(filedata)
				.imageName(file.getOriginalFilename())
				.imageType(file.getContentType())
				.build();
		return ResponseEntity.ok(service.updateAdvertiserDetails(advertiser, email));
	}
	
	
}
