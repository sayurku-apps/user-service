package com.sayurku.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Service lain gagal dihubungi atau membalas error
@Getter
public class UpstreamException extends RuntimeException {
    private final HttpStatus status;

    public UpstreamException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
