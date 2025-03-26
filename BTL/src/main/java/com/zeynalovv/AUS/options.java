package com.zeynalovv.AUS;

import com.zeynalovv.Loader;

import java.nio.file.Path;

public enum options{
    folderPath(){
        @Override
        public void setOptions(Loader load, String value){
            load.setFolderPath(Path.of(value));
        }
    },
    serverIP(){
        @Override
        public void setOptions(Loader load, String value){
            load.setServerIP(value);
        }
    },
    usernameFTP(){
        @Override
        public void setOptions(Loader load, String value){
            load.setUsernameFTP(value);
        }
    },
    passwordFTP(){
        @Override
        public void setOptions(Loader load, String value){
            load.setPasswordFTP(value);
        }
    };
    public abstract void setOptions(Loader load, String value);
}