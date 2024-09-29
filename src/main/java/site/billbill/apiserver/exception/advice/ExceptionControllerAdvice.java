package site.billbill.apiserver.exception.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import site.billbill.apiserver.exception.CustomException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchElement(NoSuchElementException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("404");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("400");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ExceptionResponse> handlerHttpServerErrorException(HttpServerErrorException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode(ex.getStatusCode().toString());
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, ex.getStatusCode());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handlerBloomCustomException(CustomException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode(ex.getStatus().toString());
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, ex.getStatus());
    }
}