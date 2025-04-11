package com.zeynalovv.AUC.ExceptionAUC;

public class BuildException extends RuntimeException {
    public BuildException() {
        super("No such path exists, or you did not build the class!");
    }
}
