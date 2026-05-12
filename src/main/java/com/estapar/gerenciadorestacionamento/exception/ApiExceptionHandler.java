package com.estapar.gerenciadorestacionamento.exception;

import com.estapar.gerenciadorestacionamento.dto.ErrorResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		return build(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(this::formatFieldError)
				.orElse("Invalid request body");
		return build(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableMessage() {
		return build(HttpStatus.BAD_REQUEST, "Invalid request body");
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
		return build(HttpStatus.BAD_REQUEST, "Required parameter is missing: " + ex.getParameterName());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName());
	}

	private String formatFieldError(FieldError error) {
		return error.getField() + " " + error.getDefaultMessage();
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
		ErrorResponse response = new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message);
		return ResponseEntity.status(status).body(response);
	}
}
