package com.zeynalovv.BI4L.ExceptionLibrary;

public class BuildException extends RuntimeException {
    public BuildException() {
        super("No such path exists, or the class is not initiated!");
    }
}
