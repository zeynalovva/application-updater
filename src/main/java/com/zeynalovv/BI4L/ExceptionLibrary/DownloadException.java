package com.zeynalovv.BI4L.ExceptionLibrary;

public class DownloadException extends RuntimeException {
    public DownloadException(String message) {
        super("Connection refused!: " + message);
    }
}
