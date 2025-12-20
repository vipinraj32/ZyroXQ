package xyz.zyro.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.servers.Server;
import xyz.zyro.config.IPFSConfig;
import xyz.zyro.dto.ShowCampaignDTO;

@Service
public class IPFSReadService {
	
	 private final RestTemplate restTemplate = new RestTemplate();
	 private static final String IPFS_GATEWAY = "https://ipfs.io/ipfs/";
	 public ShowCampaignDTO getMetadata(String cid) {

	        String metaUrl = IPFS_GATEWAY + cid + "/metadata.json";
	        return restTemplate.getForObject(metaUrl, ShowCampaignDTO.class);
	    }

	    public byte[] getFileBytes(String cid, String fileName) {

	        String fileUrl = IPFS_GATEWAY + cid + "/" + fileName;
	        return restTemplate.getForObject(fileUrl, byte[].class);
	    }
	}


