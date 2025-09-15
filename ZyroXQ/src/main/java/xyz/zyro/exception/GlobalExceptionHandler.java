package xyz.zyro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAlreadyExistException.class)
	  @ResponseStatus(HttpStatus.CONFLICT)
		@ResponseBody
		public ErrorResponse handleResourseAlreadyExistException(ResourceAlreadyExistException exception) {
			return new ErrorResponse(exception.getMessage(),HttpStatus.CONFLICT.value());
		}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
		return new ErrorResponse(exception.getMessage(),HttpStatus.NOT_FOUND.value());
	}
	
	@ExceptionHandler(ResolverException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorResponse handleResolverException(ResolverException exception) {
		
		return new ErrorResponse(exception.getMessage(),HttpStatus.FORBIDDEN.value());
	}
}
