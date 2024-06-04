package ua.team3.carsharingservice.exception;

import jakarta.persistence.EntityNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERRORS = "errors";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put(ERRORS, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            NoCarsAvailableException.class
    })
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return getDefaultTemplate(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            SQLIntegrityConstraintViolationException.class,
            DataIntegrityViolationException.class,
    })
    public ResponseEntity<Object> handleSqlIntegrityConstraintViolationException(
            Exception e) {
        return getDefaultTemplate(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            NotValidRentalDateException.class,
            NotValidReturnDateException.class
    })
    public ResponseEntity<Object> handleBadRequestException(
            Exception e) {
        return getDefaultTemplate(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ForbiddenRentalCreationException.class,
            RentalAlreadyReturnedException.class
    })
    public ResponseEntity<Object> handleForbiddenRequestException(
            Exception e) {
        return getDefaultTemplate(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotificationSendingException.class)
    public ResponseEntity<Object> handleNotificationSendingException(
            Exception e
    ) {
        return getDefaultTemplate(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> getDefaultTemplate(Throwable e, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status);
        body.put(ERRORS, e.getMessage());
        return new ResponseEntity<>(body, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + ": " + message;
        }
        return e.getDefaultMessage();
    }
}
