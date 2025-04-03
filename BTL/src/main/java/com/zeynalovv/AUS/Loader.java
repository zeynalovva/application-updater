package com.zeynalovv.AUS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public final class Loader {
    private Path folderPath, serverPath;
    private String serverIP, usernameFTP, passwordFTP;

    public Loader(){}
    public Loader(Path applicationDir,  Path tempDir){
        Path option = applicationDir.resolve("options.txt");
        try {
            BufferedReader read = new BufferedReader(new FileReader(String.valueOf(option)));
            String line;
            while((line = read.readLine()) != null){
                initialization(line, this);
            }
        } catch (IOException e) {
            try{
                Path tempPath = tempDir.resolve("YGG97ak4994hJ6nok4Pagg.txt");
                BufferedReader read = new BufferedReader(new FileReader(String.valueOf(tempPath)));
                String line;
                while((line = read.readLine()) != null){
                    initialization(line, this);
                }
            } catch (IOException ex) {
                System.out.println("Could not load the options! Check out options.txt file!");
                return;
            }
        }
    }

    private void initialization(String line, Loader load){
        String[] t = line.split("=");
        for(options i : options.values()){
            if(i.name().equals(t[0])){
                i.setOptions(load, t[1]);
            }
        }
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

}
