package com.zeynalovv.AUC.updaterExceptions;

public class DownloadException extends RuntimeException {
    public DownloadException(String message) {
        super("Connection refused!: " + message);
    }
}
