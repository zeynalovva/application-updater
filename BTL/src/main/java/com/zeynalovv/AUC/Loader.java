package com.zeynalovv.AUC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class Loader {
    private URI server;
    private String version, appFolder;
    private Path currenDir;

    public Loader(Path applicationDir) throws IOException {
        this.currenDir = applicationDir;
        Path option = applicationDir.resolve("options.txt");
        try {
            BufferedReader read = new BufferedReader(new FileReader(String.valueOf(option)));
            String line;
            while((line = read.readLine()) != null){
                initialization(line, this);
            }
        } catch (IOException e) {
            throw new IOException();
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


    public void setAppFolder(String appFolder) {
        this.appFolder = appFolder;
    }

    public String getAppFolder() {
        return appFolder;
    }

    public Path getCurrenDir() {
        return currenDir;
    }

    public void setServer(URI server) {
        this.server = server;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URI getServer() {
        return server;
    }

    public String getVersion() {
        return version;
    }
}
