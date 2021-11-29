package fi.digitalentconsulting.colors.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ColorAdvice {

	@ExceptionHandler( value= { NoSuchElementException.class} )
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ExceptionMessage> handlNoSuchElementException(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ExceptionMessage("Not found", ex.getMessage()));
	}
	
}

