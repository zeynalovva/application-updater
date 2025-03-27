package com.zeynalovv.AUS;

import java.nio.file.Path;

public final class Loader {
    private Path folderPath, serverPath;
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

    public void setServerPath(Path serverPath) {
        this.serverPath = serverPath;
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

    public Path getServerPath() {
        return serverPath;
    }

    //@Override
    //public String toString() {
    //    return this.folderPath + " " + this.usernameFTP + " " + this.passwordFTP + " " + this.serverIP;
    //}
}
