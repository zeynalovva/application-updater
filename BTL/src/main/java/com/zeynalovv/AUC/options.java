package com.zeynalovv.AUC;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


public enum options{
    SERVER(){
        @Override
        public void setOptions(Loader load, String value){
            StringBuilder full = new StringBuilder("http://");
            //StringBuilder app = new StringBuilder();
            full.append(value);
            String appFolder = value.substring(value.indexOf('/'));
            load.setAppFolder(appFolder);
            load.setServer(URI.create(full.toString()));
        }
    },
    VERSION(){
        @Override
        public void setOptions(Loader load, String value) {
            load.setVersion(value);
        }
    };
    public abstract void setOptions(Loader load, String value);
}
