package com.gustavo.tripplanner.auth;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("O e-mail '" + email + "' já está em uso");
    }
}
