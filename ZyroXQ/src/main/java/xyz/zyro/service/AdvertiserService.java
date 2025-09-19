package xyz.zyro.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import xyz.zyro.controller.AdvertiserController;
import xyz.zyro.dto.AdvertiserDTO;
import xyz.zyro.entity.Advertiser;
import xyz.zyro.entity.User;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.AdvertiserRepository;
import xyz.zyro.repository.UserRepository;

@Service
@AllArgsConstructor
public class AdvertiserService {

    private AdvertiserRepository advertiserRepository;
    private UserRepository userRepository;
    
    @Transactional
    public AdvertiserDTO updateAdvertiserDetails(Advertiser advertiser) {
    	User user=userRepository.findByEmail(advertiser.getEmail()).orElseThrow(()->new ResourceNotFoundException("Updation failed. check your email"));
    	user.setAdvertiser(advertiser);
    	user.setProfileStatus(true);
    	advertiserRepository.save(advertiser);
    	userRepository.save(user);
    	
    	return new AdvertiserDTO(advertiser.getName(),advertiser.getCompanyName(),advertiser.getWalletAddres(),advertiser.getStakeAmount(),advertiser.getMobile());
    }
    
    public String show() {
    	return "Advertidser show Call";
    }
}
