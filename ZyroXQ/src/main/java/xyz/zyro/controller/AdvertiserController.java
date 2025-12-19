package xyz.zyro.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.dto.CampaignCreateDTO;
import xyz.zyro.dto.StoryCampaignDTO;
import xyz.zyro.entity.Advertiser;
import xyz.zyro.exception.CustomIOException;
import xyz.zyro.service.AdvertiserService;
@RestController
@RequestMapping("/advertiser")
@CrossOrigin(origins = "http://localhost:8081")
public class AdvertiserController {
	
	@Autowired
	private AdvertiserService service;
	
	@PostMapping("/update-details")
	public ResponseEntity<AdvertiserDTO> updateAdvertiser(@Valid @RequestParam("companyName")String companyName, @RequestParam("file")MultipartFile file , @RequestParam("email")String email){
		byte[] filedata;
		try {
		filedata=file.getBytes();
		}catch (IOException e) {
			throw new CustomIOException("Failed To update Profile details:"+e.getMessage());
		}
		Advertiser advertiser=Advertiser.builder()
				.companyName(companyName)
				.imageData(filedata)
				.imageName(file.getOriginalFilename())
				.imageType(file.getContentType())
				.build();
		return ResponseEntity.ok(service.updateAdvertiserDetails(advertiser, email));
	}
	
	@GetMapping("/get-details")
	public ResponseEntity<AdvertiserDTO> getProfileDetails(@RequestParam String email) {
		
		return ResponseEntity.ok(service.getProfileDetails(email));
				
	}
	
	@GetMapping("/update-wallet")
	public ResponseEntity<String> updateWalletAddress(@RequestParam("compnayName")String companyName, @RequestParam("walletAddress")String walletAddress){	
		return ResponseEntity.ok(service.updateWalletAddress(walletAddress, companyName));
		
	}
	
	@GetMapping("/create-campaign")
	public ResponseEntity<String> createCampaign(@RequestParam("companyName") @Valid String companyName,@RequestParam("requirment")@Valid String requirment, @RequestParam("date")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date ,@RequestParam("campaignType")@Valid String campaignType, @RequestParam("totalCampaign")Integer totalCampaign, @RequestParam("view")@Valid Integer view, @RequestParam("amount")@Valid Integer amount,  @RequestParam("file") MultipartFile file){
		CampaignCreateDTO dto=new CampaignCreateDTO(companyName,requirment,date,campaignType,totalCampaign,view,amount);
		return ResponseEntity.ok(service.createCampaign(dto, file));
	}
	
	
	@GetMapping("/create")
	public ResponseEntity<String> create(@RequestParam("file") MultipartFile file){
		
		return ResponseEntity.ok(service.create(file));
	}
	
	@GetMapping("/get/{cid}")
	public ResponseEntity<?> getDataIPFS(@PathVariable String cid){
		try {
			CampaignCreateDTO campaignCreateDTO=service.fetchJson(cid, CampaignCreateDTO.class);
			return ResponseEntity.ok(campaignCreateDTO);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error parsing JSON: " + e.getMessage());
		}
		
	}
	
    @GetMapping("/craete-story")
	public ResponseEntity<String> craeteStory(@RequestParam("campaignId")Integer campaignId, @RequestParam("companyName")String companyName, @RequestParam("payPerInfluncer")Integer payPerInfluncer, @RequestParam("budget")Integer budget, @RequestParam("requiment")String requiment,  @RequestParam("date")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam("file")MultipartFile file){
		 LocalDate currentDate = LocalDate.now();
		 StoryCampaignDTO dto=new StoryCampaignDTO(campaignId, companyName, payPerInfluncer, budget, requiment, currentDate, date);
		 return ResponseEntity.ok(service.createStoryCampaign(dto, file));
	}
}
