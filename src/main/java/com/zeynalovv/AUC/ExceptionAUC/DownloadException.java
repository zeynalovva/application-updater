package com.zeynalovv.AUC.ExceptionAUC;

public class DownloadException extends RuntimeException {
    public DownloadException(String message) {
        super("Connection refused!: " + message);
    }
}
