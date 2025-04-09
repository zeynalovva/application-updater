package com.zeynalovv.AUC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class Loader {
    private URI server = null;
    private String version = "";
    private Path appPath = null;


    private Loader(Builder build){
        this.server = build.server;
        this.appPath = build.appPath;
        this.version = build.version;
    }

    public Path getAppPath() {
        return appPath;
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


    public static class Builder{
        private URI server = null;
        private String version = "";
        private Path appPath = null;


        public Builder (Path appPath){
            this.appPath = appPath;
        }

        public Builder server(String server){
            this.server = URI.create(server);
            return this;
        }

        public Builder version(String version){
            this.version = version;
            return this;
        }

        public Loader build(){
            return new Loader(this);
        }

    }
}


