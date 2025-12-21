package xyz.zyro.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.dto.ShowCampaignDTO;
import xyz.zyro.service.AdvertiserService;
import xyz.zyro.service.IPFSReadService;

@RestController
@RequestMapping("/zyro")
@CrossOrigin(origins = "http://localhost:8081")
public class AllDetailsController {

	
	@Autowired
	private AdvertiserService service;
	@Autowired
	private IPFSReadService readService;
	
	@GetMapping("/get-Details")
	public ResponseEntity<AdvertiserDTO> getProfileDetails() {
		
		return ResponseEntity.ok(service.getProfileDetails("vipendrarajpoot2@gmail.com"));
				
	}

	    @GetMapping("/campaign")
	    public ResponseEntity<Map<String, Object>> viewCampaign(@RequestParam("cid") String cid) {

	        ShowCampaignDTO meta = readService.getMetadata(cid);

	        Map<String, Object> response = new HashMap<>();
	        response.put("campaignId", meta.getCampaignId());
	        response.put("companyName", meta.getCompanyName());
	        response.put("payPerInfluncer", meta.getPayPerInfluncer());
	        response.put("budget", meta.getBudget());
	        response.put("requiment", meta.getRequiment());
	        response.put("startDate", meta.getStartDate());
	        response.put("endDate", meta.getEndDate());

	        response.put(
	            "downloadUrl",
	            "/api/ipfs/download/" + cid
	        );

	        return ResponseEntity.ok(response);
	    }

	    @GetMapping("/download/{cid}")
	    public ResponseEntity<byte[]> downloadFile(@PathVariable String cid) {

	        ShowCampaignDTO meta = readService.getMetadata(cid);
	        byte[] fileBytes = readService.getFileBytes(cid, meta.getFileName());

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType(meta.getFileType()));
	        headers.setContentDisposition(
	                ContentDisposition.attachment()
	                        .filename(meta.getFileName())
	                        .build()
	        );

	        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
	    }
	}
	
	

