package xyz.zyro.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
	
	@ExceptionHandler(CustomIOException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ResponseBody
	public ErrorResponse handleCustomIOException(CustomIOException exception) {
		
		return new ErrorResponse(exception.getMessage(),HttpStatus.NOT_ACCEPTABLE.value());
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentException(MethodArgumentNotValidException exception) {
		Map<String, String>response=new HashMap<>();
		BindingResult bindingResult=exception.getBindingResult();
		List<FieldError> errorList=bindingResult.getFieldErrors();
		for(FieldError error:errorList) {
			response.put(error.getField(), error.getDefaultMessage());
		}
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
		
	}
}
