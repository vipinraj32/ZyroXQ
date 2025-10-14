package xyz.zyro.security;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.exception.ResolverException;

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
//	                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
	                .authorizeHttpRequests(auth -> auth
	                        .requestMatchers("/user/login","/user/advertiser/login","/user/signup","/user/run").permitAll()
	                        .requestMatchers(HttpMethod.POST, "/influncer/update-details").authenticated() 
	                        .requestMatchers(HttpMethod.POST, "/advertiser/update-details").authenticated()
	                        .requestMatchers(HttpMethod.POST, "/advertiser/get-details").authenticated()
	                        .requestMatchers(HttpMethod.GET, "/run").authenticated()
	                        .anyRequest().authenticated()
	                )
	                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	                .oauth2Login(oAuth2 -> oAuth2
	                        .failureHandler((request, response, exception) -> {
	                            log.error("OAuth2 error: {}", exception.getMessage());
//	                            handlerExceptionResolver.resolveException(request, response, null, exception);
//	                            throw new ResolverException(exception.getMessage());
	                            sendErrorResponse(response,exception.getMessage(), HttpStatus.FORBIDDEN.value());
	                        })
	                        .successHandler(oAuth2SuccessHandler)
	                         
	                        ); 

//	                .formLogin();
	        return httpSecurity.build();
	    }
	  
	  private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
		    response.setStatus(statusCode);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");

		    String json = String.format(
		        "{ \"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\" }",
		        java.time.LocalDateTime.now(),
		        statusCode,
		        HttpStatus.valueOf(statusCode).getReasonPhrase(),
		        message,
		        "" // optionally: request.getRequestURI()
		    );

		    response.getWriter().write(json);
		}
}
