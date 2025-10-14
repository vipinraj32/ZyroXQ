package xyz.zyro.service;

import java.util.Set;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.dto.LoginRequest;
import xyz.zyro.dto.Response;
import xyz.zyro.entity.User;
import xyz.zyro.entity.type.AuthProviderType;
import xyz.zyro.entity.type.Role;
import xyz.zyro.exception.ResourceAlreadyExistException;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.RoleRepository;
import xyz.zyro.repository.UserRepository;
import xyz.zyro.security.AuthenticateUtill;

@Service
@Slf4j
public class AuthenticatService {
	@Autowired	
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticateUtill authenticateUtill;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
    private  PasswordEncoder passwordEncoder;
	
	@Autowired
	private  OAuth2AuthorizedClientService authorizedClientService;
	
	public Response login(LoginRequest request) {
//		log.info(request.getPassword());
//		User u=userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword()).orElseThrow(()->new ResourceNotFoundException("Invlid email/password"));
		
	     Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
	        );
	     
	     User user = (User) authentication.getPrincipal();

	        String token = authenticateUtill.generateAccessToken(user);

	        return new Response(token, user.getEmail(), user.getAuthorities().toString(), user.getUsername(), user.getProfileStatus());
	    }
	   public User signUpInternal(LoginRequest signupRequestDto, AuthProviderType authProviderType,String name, String providerId, Integer roleId, String accessToken) {
	        User user = userRepository.findById(signupRequestDto.getEmail()).orElse(null);

	        if(user != null) throw new ResourceAlreadyExistException("User already exists");
	         Role role=roleRepository.findById(roleId).orElse(null);
	        user = User.builder()
	                .email(signupRequestDto.getEmail())
	                .userName(name)
//	                .password(signupRequestDto.getPassword())
	                .providerId(providerId)
	                .providerType(authProviderType)
	                .profileStatus(false)
	                .roles(Set.of(role))// Role.PATIENT
	                .accessToken(accessToken)
	                .build();

	        if(authProviderType == AuthProviderType.EMAIL) {
	            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
	        }
//	        user.addRole(role);
	        user = userRepository.save(user);

	       return user;
	    }
	
	   
	  @Transactional
	    public User signup(User signupRequestDto) {
	    	LoginRequest request=LoginRequest.builder()
	    			.email(signupRequestDto.getEmail())
	    			.password(signupRequestDto.getPassword())
	    			.build();
	        User user = signUpInternal(request, AuthProviderType.EMAIL,signupRequestDto.getUsername(), null,signupRequestDto.getRoleId(),null);
	        return user;
	    }
	  
	  
	  @Transactional
	    public ResponseEntity<Response> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId, OAuth2AuthenticationToken authentication) {
	        AuthProviderType providerType = authenticateUtill.getProviderTypeFromRegistrationId(registrationId);
	        String providerId = authenticateUtill.determineProviderIdFromOAuth2User(oAuth2User, registrationId);
	        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
	                authentication.getAuthorizedClientRegistrationId(),
	                authentication.getName());
	        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
	        log.info(client.toString());
	        String email = "";
	        String accessToken = client != null ? client.getAccessToken().getTokenValue() : "";
	        String name = oAuth2User.getAttribute("name");
	        if(providerType==AuthProviderType.INSTAGRAM)
	        	email=oAuth2User.getAttribute("username");
	        else
	        email=oAuth2User.getAttribute("email");
	        
//	      
	        log.info(name);
	        log.info(oAuth2User.toString());

	        User emailUser = userRepository.findByEmail(email).orElse(null);

	        if(user == null && emailUser == null) {
	            // signup flow:
	            String useremail = authenticateUtill.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
	            user = signUpInternal(new LoginRequest(useremail, null), providerType,name, providerId,2,accessToken);
	        } else if(user != null) {
	            if(email != null && !email.isBlank() && !email.equals(user.getUsername())) {
	                user.setEmail(email);
	                userRepository.save(user);
	            }
	        } else {
	            throw new BadCredentialsException("This email is already registered with provider "+emailUser.getProviderType());
	        }

	        Response loginResponseDto = new Response(authenticateUtill.generateAccessToken(user), user.getEmail(), user.getAuthorities().toString(),user.getUsername(),user.getProfileStatus());
	        return ResponseEntity.ok(loginResponseDto);
	    }
	}
	

