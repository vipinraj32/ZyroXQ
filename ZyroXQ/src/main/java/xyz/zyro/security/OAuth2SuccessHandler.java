package xyz.zyro.security;

import java.io.IOException;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import xyz.zyro.dto.Response;
import xyz.zyro.service.AuthenticatService;
@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private AuthenticatService authenticatService;
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		 OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
	        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

	        String registrationId = token.getAuthorizedClientRegistrationId();
            
	        ResponseEntity<Response> loginResponse = authenticatService.handleOAuth2LoginRequest(oAuth2User,
	                registrationId,token);

	        response.setStatus(loginResponse.getStatusCode().value());
	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	        response.getWriter().write(objectMapper.writeValueAsString(loginResponse.getBody()));
		
	}

}
