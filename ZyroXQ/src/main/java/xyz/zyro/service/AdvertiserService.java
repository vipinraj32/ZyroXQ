package xyz.zyro.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.controller.AdvertiserController;
import xyz.zyro.dto.AdvertiserDTO;
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
}
