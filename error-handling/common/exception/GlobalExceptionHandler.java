package com.astronity.pos.common.exception;

import com.astronity.pos.common.response.ApiError;
import com.astronity.pos.common.response.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Turns every exception into ApiResponse.fail(...) with the right HTTP status.
 * One method per kind of error; Spring picks the method whose exception type matches best.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Our own exceptions: OrderNotFoundException, InvalidCredentialsException, ...
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return fail(ex.getStatus(), ex.getCode(), ex.getMessage());
    }

    // @Valid failed on a request body: list every invalid field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ApiError.FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        ApiError error = new ApiError("VALIDATION_FAILED", "Some fields are invalid", details);
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    // Broken or missing JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Void>> handleBadJson(HttpMessageNotReadableException ex) {
        return fail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "The request body is missing or isn't valid JSON");
    }

    // A query or path value is missing or has the wrong type, e.g. ?status=nope
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiResponse<Void>> handleBadParameter(Exception ex) {
        return fail(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "A request parameter is missing or invalid");
    }

    // Unknown URL
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleUnknownUrl(NoResourceFoundException ex) {
        return fail(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested resource doesn't exist");
    }

    // e.g. DELETE on a URL that only supports GET
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> handleWrongMethod(HttpRequestMethodNotSupportedException ex) {
        return fail(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "This HTTP method isn't supported here");
    }

    // Not signed in
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiResponse<Void>> handleNotSignedIn(AuthenticationException ex) {
        return fail(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Sign in to continue");
    }

    // Signed in, but not allowed
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handleForbidden(AccessDeniedException ex) {
        return fail(HttpStatus.FORBIDDEN, "FORBIDDEN", "You don't have permission to do this");
    }

    // Anything else. Other Spring request errors keep their own status (e.g. 415);
    // everything unexpected is logged and becomes a 500 without internal details.
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        if (ex instanceof ErrorResponse springError) {
            return fail(springError.getStatusCode(), "REQUEST_FAILED", "The request couldn't be processed");
        }
        log.error("Unexpected error", ex);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Something went wrong");
    }

    private static ResponseEntity<ApiResponse<Void>> fail(HttpStatusCode status, String code, String message) {
        return ResponseEntity.status(status).body(ApiResponse.fail(ApiError.of(code, message)));
    }
}
