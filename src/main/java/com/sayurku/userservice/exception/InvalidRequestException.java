package com.sayurku.userservice.exception;

// 400: request-nya terbaca, tapi isinya tidak masuk akal (mis. STAFF tanpa cabang)
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
