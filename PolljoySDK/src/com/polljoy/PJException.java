package com.polljoy;

public class PJException extends RuntimeException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a new PJException.
     */
    public PJException() {
        super();
    }

    /**
     * Constructs a new PJException.
     * 
     * @param message
     *            the detail message of this exception
     */
    public PJException(String message) {
        super(message);
    }

    /**
     * Constructs a new PJException.
     * 
     * @param message
     *            the detail message of this exception
     * @param throwable
     *            the cause of this exception
     */
    public PJException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new PJException.
     * 
     * @param throwable
     *            the cause of this exception
     */
    public PJException(Throwable throwable) {
        super(throwable);
    }
}