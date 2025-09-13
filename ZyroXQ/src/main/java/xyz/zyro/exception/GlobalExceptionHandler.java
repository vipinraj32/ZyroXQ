package xyz.zyro.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAlreadyExistException.class)
	  @ResponseStatus(HttpStatus.CONFLICT)
		@ResponseBody
		public ErrorResponse handleResourseAlreadyExistException(ResourseAlreadyExistException exception) {
			return new ErrorResponse(exception.getMessage(),HttpStatus.CONFLICT.value());
		}
}
