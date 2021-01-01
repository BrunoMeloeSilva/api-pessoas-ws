package com.github.brunomeloesilva.pessoas.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.github.brunomeloesilva.pessoas.api.exceptionhandler.ProblemDetails.InvalidParams;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override 
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		var message = String.format("The resource %s, which you tried to access, is non-existent.", ex.getRequestURL());
		var body = errorMessage(status, message, null);
		
		return handleExceptionInternal(ex, body, headers, status, request);
	}
	
	
	@Override 
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		Throwable rootCause = ExceptionUtils.getRootCause(ex);
		String message = "You must send a JSON with the correct structure.";
		
		var body = errorMessage(status, message, null);
		
		if (rootCause instanceof InvalidFormatException) {
			
			var e = (InvalidFormatException) rootCause;
			message = String.format("Invalid type value. The '%1$s' property is of type '%2$s', for "
					+ "this reason the assignment, %1$s=%3$s, is invalid in the submitted JSON."
					, joinPath(e.getPath()) ,e.getTargetType().getSimpleName(), e.getValue());
			body.setDetail(message);
			
			return handleExceptionInternal(e, body, headers, status, request);
			
		}else if(rootCause instanceof JsonParseException) {
			var e = (JsonParseException) rootCause;
			body.setDetail(e.getLocalizedMessage());
			
			return handleExceptionInternal(e, body, headers, status, request);
		}
		
		return handleExceptionInternal(ex, body, headers, status, request);
	}
	
	
	@Override 
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		BindingResult bindingResult = ex.getBindingResult();
		List<InvalidParams> problemFields = bindingResult.getFieldErrors()
				.stream()
				.map(fieldError -> new InvalidParams(fieldError.getField() ,fieldError.getDefaultMessage()))
				.collect(Collectors.toList());
		
		var body = errorMessage(status, "One or more invalid or nonexistent attributes.", problemFields); 
		return handleExceptionInternal(ex, body, headers, status, request);
	}
	
	
	
	@ExceptionHandler(ConstraintViolationException.class) 
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
		
		var message = "One or more attributes sent is invalid.";
		var exception = (JDBCException) ex;
		var sqlException = exception.getSQLException();
		int sqlErrorCode = sqlException.getErrorCode();
		
		if(sqlErrorCode == 1062) {
			String[] partesMsgErro = (sqlException.getLocalizedMessage().split("'"));
			String valor = partesMsgErro[1];
			String[] objetoErro = partesMsgErro[3].split("_");
			int indexPropriedade = objetoErro[0].indexOf('.') + 1;
			String propriedade = objetoErro[0].substring(indexPropriedade);
			
			message = String.format("Duplicate value, the %s %s has already been registered.",propriedade ,valor);
		}else if(sqlErrorCode == 1048) {
			message = sqlException.getLocalizedMessage();
		}
		
		var httpStatus = HttpStatus.BAD_REQUEST;
		var body = errorMessage(httpStatus, message, null);
			
		return handleExceptionInternal(ex, body, null, httpStatus, request);
	}	
	
	/** METODOS UTILITÁRIOS */
	private ProblemDetails errorMessage(HttpStatus status, String errorMessage, List<InvalidParams> invalidParams) {
		return new ProblemDetails(status.value()
				, OffsetDateTime.now()
				, status.getReasonPhrase()
				, errorMessage
				, invalidParams);
	}
	
	private String joinPath(List<Reference> references) {
		return references.stream()
			.map(ref -> ref.getFieldName())
			.collect(Collectors.joining("."));
	}
}