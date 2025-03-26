package com.zeynalovv;

import java.nio.file.Path;

public class Loader {
    private Path folderPath;
    private String serverIP, usernameFTP, passwordFTP;

    public Loader(Path folderPath){
        this.folderPath = folderPath;
    }

    public void setFolderPath(Path folderPath) {
        this.folderPath = folderPath;
    }

    public void setPasswordFTP(String passwordFTP) {
        this.passwordFTP = passwordFTP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setUsernameFTP(String usernameFTP) {
        this.usernameFTP = usernameFTP;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    public String getPasswordFTP() {
        return passwordFTP;
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getUsernameFTP() {
        return usernameFTP;
    }

    @Override
    public String toString() {
        return this.folderPath + " " + this.usernameFTP + " " + this.passwordFTP + " " + this.serverIP;
    }
}
