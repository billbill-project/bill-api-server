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
    public ResponseEntity<ExceptionResponse.Payload> handleNoSuchElement(NoSuchElementException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse(
                false,
                new ExceptionResponse.Payload(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase()
                )
        );
        return new ResponseEntity<>(exceptionResult.getData(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse.Payload> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse(
                false,
                new ExceptionResponse.Payload(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                )
        );
        return new ResponseEntity<>(exceptionResult.getData(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ExceptionResponse.Payload> handlerHttpServerErrorException(HttpServerErrorException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse(
                false,
                new ExceptionResponse.Payload(
                        ex.getStatusCode().value(),
                        ex.getMessage()
                )
        );
        return new ResponseEntity<>(exceptionResult.getData(), ex.getStatusCode());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse.Payload> handlerBloomCustomException(CustomException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse(
                false,
                new ExceptionResponse.Payload(
                        ex.getErrorCode().getCode(),
                        ex.getMessage()
                )
        );
        System.out.println(ex.getStatus());
        return new ResponseEntity<>(exceptionResult.getData(), ex.getStatus());
    }
}