package com.zeynalovv.BI4L.ExceptionLibrary;

public class LoaderNullException extends RuntimeException {
    public LoaderNullException() {
        super("The file set as for the loading settings, could not be read properly");
    }
}
