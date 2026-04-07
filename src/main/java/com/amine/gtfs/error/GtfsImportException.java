package com.amine.gtfs.error;

public class GtfsImportException extends RuntimeException {
    public GtfsImportException(String message) {
        super(message);
    }

    public GtfsImportException(String message, Throwable cause) {
        super(message, cause);//Exception Chaining
    }
}
