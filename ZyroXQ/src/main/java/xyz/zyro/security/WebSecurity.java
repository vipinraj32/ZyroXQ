package xyz.zyro.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@AllArgsConstructor
@Slf4j
@EnableMethodSecurity
public class WebSecurity {
	
	private final JwtAuthenticateFilter jwtAuthFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	  private final HandlerExceptionResolver handlerExceptionResolver;

	  @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	        httpSecurity
	                .csrf(csrfConfig -> csrfConfig.disable())
	                .sessionManagement(sessionConfig ->
	                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	                .authorizeHttpRequests(auth -> auth
	                        .requestMatchers("/user/login","/user/advertiser/login","/user/signup").permitAll()
	                        .anyRequest().authenticated()
	                )
	                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	                .oauth2Login(oAuth2 -> oAuth2
	                        .failureHandler((request, response, exception) -> {
	                            log.error("OAuth2 error: {}", exception.getMessage());
	                            handlerExceptionResolver.resolveException(request, response, null, exception);
	                        })
	                        .successHandler(oAuth2SuccessHandler)
	                        ); 

//	                .formLogin();
	        return httpSecurity.build();
	    }
}
