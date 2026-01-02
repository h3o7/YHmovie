package com.yhmovie.common.handler;

import com.yhmovie.common.exception.DBException;
import com.yhmovie.common.exception.InsufficientPermissionException;
import com.yhmovie.common.exception.RateLimitException;
import com.yhmovie.common.exception.ResourceNotFoundException;
import com.yhmovie.pojo.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DBException.class)
    public Result DBException(DBException e) {
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Result handleResourceNotFoundException(ResourceNotFoundException e) {
        return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public Result handleInsufficientPermissionException(InsufficientPermissionException e) {
        return Result.error(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(RateLimitException.class)
    public Result handleRateLimitException(RateLimitException e) {
        return Result.error(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }






}
