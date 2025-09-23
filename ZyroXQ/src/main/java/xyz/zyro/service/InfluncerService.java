package xyz.zyro.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.dto.InfluncerDTO;
import xyz.zyro.entity.Influncer;
import xyz.zyro.entity.User;
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
}
