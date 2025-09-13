package xyz.zyro.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.entity.User;

@Component("securityAuthenticateUtil")
@Slf4j
public class AuthenticateUtill {

	 @Value("${jwt.secretkey}")
	 private String jwtSecretKey;
	 
	 private SecretKey getSecretKey() {
	        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
	    }

	    public String generateAccessToken(User user) {
	        return Jwts.builder()
	                .subject(user.getEmail())
	                .claim("username",user.getUsername())
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
	                .signWith(getSecretKey())
	                .compact();
	    }

	
}
