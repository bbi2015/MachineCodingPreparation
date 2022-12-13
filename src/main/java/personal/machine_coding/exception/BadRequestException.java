package personal.machine_coding.exception;

import java.io.IOException;

public class BadRequestException extends IOException {

    public BadRequestException() {
        super();
    }

    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String message, final Throwable e) {
        super(message, e);
    }
}
