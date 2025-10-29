package xyz.zyro.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.dto.InfluncerDTO;
import xyz.zyro.dto.InstagramMediaResponseDTO;
import xyz.zyro.dto.ProfileDetailsDTO;
import xyz.zyro.entity.Influncer;
import xyz.zyro.entity.User;
import xyz.zyro.entity.type.AuthProviderType;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.InfluncerRepository;
import xyz.zyro.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class InfluncerService {

	private UserRepository userRepository;
	private InfluncerRepository influncerRepository;
	
	
	
	@Transactional
	public InfluncerDTO updateInfluncerDetails(Influncer influncer) {
		log.info(influncer.getEmail()+"Influncer called");
		User user=userRepository.findById(influncer.getUsername()).orElseThrow(()->new ResourceNotFoundException("Updation failed. User not register"));
		if(user.getProfileStatus()==false) {
		user.setInfluncer(influncer);
		influncerRepository.save(influncer);
		user.setProfileStatus(true);
		userRepository.save(user);
		}
		return new InfluncerDTO(influncer.getEmail(), influncer.getWalletAddress(), influncer.getName());
	}
	
	
	public ProfileDetailsDTO getProfileDetails(String username) {
		String baseUrl="https://graph.instagram.com/me?fields=id,username,followers_count,account_type,name,media_count&access_token=";
		User user=userRepository.findById(username).orElseThrow(()->new ResourceNotFoundException("User not exist.Create A Account"));
		if(user.getProviderType()==AuthProviderType.FACEBOOK) {
			baseUrl="https://graph.facebook.com/me?fields=id,username,followers_count,account_type,name,media_count&access_token=";
		}
	    String url=baseUrl+user.getAccessToken();
	    RestTemplate restTemplate = new RestTemplate();
	    return restTemplate.getForObject(url, ProfileDetailsDTO.class);
	}
	
	public InstagramMediaResponseDTO getMediaResponse(String username) {
		User user=userRepository.findById(username).orElseThrow(()->new ResourceNotFoundException("User not exist.Create A Account"));
		String url="https://graph.instagram.com/me/media?fields=id,caption,media_type,media_url,permalink,thumbnail_url,timestamp&access_token="+user.getAccessToken();
		RestTemplate template=new RestTemplate();
		return template.getForObject(url, InstagramMediaResponseDTO.class);
	}
	
	
}
