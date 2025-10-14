package xyz.zyro.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.zyro.entity.User;
import xyz.zyro.exception.ResourceNotFoundException;
import xyz.zyro.repository.UserRepository;

@Component
@Slf4j
@AllArgsConstructor
public class JwtAuthenticateFilter extends OncePerRequestFilter{
	
	private final UserRepository userRepository;
	private final AuthenticateUtill authUtil;
	private final HandlerExceptionResolver handlerExceptionResolver;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("incoming request: {}", request.getRequestURI());
	      
        String requestHeader = request.getHeader("Authorization");
        String token=null;
        String useremail=null;
        if(requestHeader!=null && requestHeader.startsWith("Bearer")){

        token = requestHeader.substring(7);
        log.info(token);
        try {
         useremail = authUtil.getUsernameFromToken(token);
        log.info(useremail);
		}catch (IllegalArgumentException e) {
			// TODO: handle exception
		logger.info("Illegal Argument while fetching the Username!!");
//			e.printStackTrace();
		}catch (ExpiredJwtException e) {
			logger.info("Given jwt Token is Expired");
//			throw new JWTExpiredException("JWT token has expired. Please login again.");
//			e.printStackTrace();
			sendErrorResponse(response, "JWT token has expired. Please login again.", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}catch (MalformedJwtException e) {
//			// TODO: handle exception
			logger.info("Some Changed has Done in token!! Invalid Token");
//			throw new JWTExpiredException("Some Changed has Done in token!! Invalid Token");
////			e.printStackTrace();
			sendErrorResponse(response, "Some Changed has Done in token!! Invalid Token.", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}catch (Exception e) {
//			e.printStackTrace();
			sendErrorResponse(response, "Authentication failed. Your session token is invalid or has been tampered with. Please log in again", HttpServletResponse.SC_UNAUTHORIZED);
			return;
      }
     }else {
	     logger.info("Invlid Header value!!");
  }
        

        if (useremail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(useremail).orElseThrow(()-> new  ResourceNotFoundException("Invalid Token"));
            log.info(user.getUsername());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            log.info(usernamePasswordAuthenticationToken.toString());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            
        }
        filterChain.doFilter(request, response);

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
