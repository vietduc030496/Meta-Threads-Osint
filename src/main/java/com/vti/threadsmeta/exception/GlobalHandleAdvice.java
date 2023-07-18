package com.vti.threadsmeta.exception;

import com.vti.threadsmeta.dto.common.ErrorResponse;
import com.vti.threadsmeta.exception.custom.ThreadsItemNotFound;
import com.vti.threadsmeta.exception.custom.ThreadsUserNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class GlobalHandleAdvice {

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        log.info(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                ex.getMessage(),
                "");

        return ResponseEntity.status(HttpStatus.OK).body(error);
    }

    @ExceptionHandler({ThreadsUserNotFound.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleThreadUserNotFound(ThreadsUserNotFound ex, WebRequest request) {
        log.info(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                ex.getMessage(),
                ex.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(error);
    }

    @ExceptionHandler({ThreadsItemNotFound.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleThreadUserNotFound(ThreadsItemNotFound ex, WebRequest request) {
        log.info(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                ex.getMessage(),
                ex.getItem());

        return ResponseEntity.status(HttpStatus.OK).body(error);
    }
}
