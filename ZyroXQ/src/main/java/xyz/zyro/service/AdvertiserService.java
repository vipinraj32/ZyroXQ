package xyz.zyro.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.config.IPFSConfig;
import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.dto.CampaignCreateDTO;
import xyz.zyro.dto.StoryCampaignDTO;
import xyz.zyro.entity.Advertiser;
import xyz.zyro.entity.User;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.AdvertiserRepository;
import xyz.zyro.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class AdvertiserService {

    private AdvertiserRepository advertiserRepository;
    private UserRepository userRepository;
    private IPFSConfig ipfsConfig;
    
    @Transactional
    public AdvertiserDTO updateAdvertiserDetails(Advertiser advertiser, String email) {
    	User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Updation failed. check your email"));
    	if(user.getProfileStatus()==false) {
    	user.setAdvertiser(advertiser);
    	user.setProfileStatus(true);
    	advertiserRepository.save(advertiser);
    	userRepository.save(user);
    	}
    	
    	return new AdvertiserDTO(advertiser.getCompanyName(),advertiser.getWalletAddres(),advertiser.getImageData(),advertiser.getImageName());
    }
    
    public AdvertiserDTO  getProfileDetails(String email) {
   
    	User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Failed To fetch:"+email+" details"));
    	Advertiser advertiser=user.getAdvertiser();
    	log.info(advertiser.getCompanyName());
    	return new AdvertiserDTO(advertiser.getCompanyName(),advertiser.getWalletAddres(),advertiser.getImageData(),advertiser
    			.getImageName());
    }
    
    public String updateWalletAddress(String walletAddress, String companyName) {
    	Advertiser advertiser=advertiserRepository.findById(companyName).orElseThrow(()->new ResourceNotFoundException("Company Not Register with name:"+companyName));
    	advertiser.setWalletAddres(walletAddress);
    	advertiserRepository.save(advertiser);
    	return advertiser.getWalletAddres();
    }
    
    
    public String createCampaign(CampaignCreateDTO dto, MultipartFile file) {
    	try {
    		IPFS ipfs=ipfsConfig.IPFSConfigs();
    		ObjectMapper mapper = new ObjectMapper();
    		mapper.registerModule(new JavaTimeModule());
    		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    		String json = mapper.writeValueAsString(dto);
    		NamedStreamable.ByteArrayWrapper metadataFile =
                    new NamedStreamable.ByteArrayWrapper("metadata.json", json.getBytes());

    		NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(file.getOriginalFilename(),file.getInputStream());
    		List<NamedStreamable> fileList = Arrays.asList(metadataFile, is);
    		log.info(fileList.toString());
    		 NamedStreamable.DirWrapper dir = new NamedStreamable.DirWrapper("campaign-folder", fileList);
    		 
    		 List<MerkleNode> nodes = ipfs.add(dir);

    	        // Directory CID (last node)
    	        MerkleNode dirNode = nodes.get(nodes.size() - 1);
    	        return dirNode.hash.toBase58();
//    		 MerkleNode node = ipfs.add(dir).get(0);

//    	        return node.hash.toBase58();
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Error whilst communicating with the IPFS node"+e);
		}
    	
    		
    }
    
    public String fetchDataFromCID(String cid) throws Exception {
//         Convert CID → MultiHash
        Multihash filePointer = Multihash.fromBase58(cid);

        // Fetch the raw file bytes
        byte[] data =ipfsConfig.IPFSConfigs().cat(filePointer);
        // Convert bytes → String
        return new String(data);
    }
    
    public CampaignCreateDTO fetchJson(String cid, Class<CampaignCreateDTO> type) throws Exception {
        Multihash filePointer = Multihash.fromBase58(cid);
        byte[] data = ipfsConfig.IPFSConfigs().cat(filePointer);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(data, type);
    }
    
    public String create(MultipartFile file) {
    	try {
    		IPFS ipfs=ipfsConfig.IPFSConfigs();
    		InputStream inputStream = new ByteArrayInputStream(file.getBytes());
    		NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(inputStream);
            MerkleNode response = ipfs.add(is).get(0);
            return response.hash.toBase58();
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Error whilst communicating with the IPFS node"+e);
		}
    	
    		
    }
    
    
    public String createStoryCampaign(StoryCampaignDTO dto, MultipartFile file) {
    	try {
    		IPFS ipfs=ipfsConfig.IPFSConfigs();
    		ObjectMapper mapper = new ObjectMapper();
    		mapper.registerModule(new JavaTimeModule());
    		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    		String json = mapper.writeValueAsString(dto);
    		NamedStreamable.ByteArrayWrapper metadataFile =
                    new NamedStreamable.ByteArrayWrapper("metadata.json", json.getBytes());

    		NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(file.getOriginalFilename(),file.getInputStream());
    		List<NamedStreamable> fileList = Arrays.asList(metadataFile, is);
    		log.info(fileList.toString());
    		 NamedStreamable.DirWrapper dir = new NamedStreamable.DirWrapper("campaign-folder", fileList);
    		 
    		 List<MerkleNode> nodes = ipfs.add(dir);

    	        // Directory CID (last node)
    	        MerkleNode dirNode = nodes.get(nodes.size() - 1);
    	        return dirNode.hash.toBase58();
//    		 MerkleNode node = ipfs.add(dir).get(0);

//    	        return node.hash.toBase58();
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Error whilst communicating with the IPFS node"+e);
		}
    	
    		
    }
    
}
