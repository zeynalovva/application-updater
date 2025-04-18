package com.zeynalovv.BI4L;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Client implements Updatable{
    private final Path localPath, remotePath;
    private final String port, username, password;
    private final String fileName, ipAddress;
    private Stream<Path> list;
    private List<Path> absoluteFile;
    private List<String> relativeDir;
    private Map<String, String> hashTable;
    private Map<Path, String> ignoreItems;


    public Client(String localPath, String fileName, String remotePath, String ipAddress,
                  String port, String username, String password, Map<Path, String> ignoreItems){

        this.localPath = Path.of(localPath);
        this.ipAddress = ipAddress;
        this.port = port;
        this.password = password;
        this.remotePath = Path.of(remotePath);
        this.username = username;
        this.fileName = fileName;
        this.ignoreItems = ignoreItems;
    }

    public void start() throws IOException, NoSuchAlgorithmException, SftpException, JSchException {
        this.absoluteFile = this.scanPath(localPath).files();
        this.relativeDir = relativize(this.scanPath(localPath).directories());

        hashTable = new HashMap<>();

        for(Path x : absoluteFile){
            hashTable.put(hashOf(x), String.valueOf(localPath.relativize(x)));
        }

        parser();
        upload();

    }

    public void upload() throws JSchException, SftpException {
        Uploader uploader = new Uploader(ipAddress, port, username, password, remotePath);
        uploader.openConnection();
        for(String i : relativeDir){
            if(uploader.createFolder(i))
                System.out.println("Created: " + i);
        }

        for (Path i : absoluteFile) {
            String filePath = String.valueOf(i);
            String serverPath = String.valueOf(remotePath.resolve(localPath.relativize(i)));
            System.out.println("Uploading: " + filePath + " - to - " + serverPath);
            uploader.uploadFile(filePath, serverPath);
        }
        uploader.close();
    }

    public void parser() throws IOException {
        ObjectMapper json = new ObjectMapper();
        ObjectNode root = json.createObjectNode();
        ObjectNode files = json.createObjectNode();
        ObjectNode folders = json.createObjectNode();
        ObjectNode translated = json.createObjectNode();
        ObjectNode ignoreList = json.createObjectNode();

        for(String i : hashTable.keySet()){
            files.put(i, hashTable.get(i));
            translated.put(i, translator(hashTable.get(i).toCharArray()));
        }

        for(String i : relativeDir){
            if(!i.equals("")) folders.put(i, "D");
        }


        for(Path i : ignoreItems.keySet()){
            if(ignoreItems.get(i) == "D") ignoreList.put(String.valueOf(localPath.relativize(i)), "D");
            else ignoreList.put(String.valueOf(localPath.relativize(i)), "F");
        }

        root.set("ignore", ignoreList);
        root.set("files", files);
        root.set("folders", folders);
        root.set("translated", translated);

        json.writeValue(new File(String.valueOf(localPath.resolve(fileName))), root);
        absoluteFile.add(localPath.resolve(fileName));

    }

    public List<String> relativize(List<Path> absolutePath){
        List<String> temp = new ArrayList<>();
        absolutePath.forEach(x -> temp.add(String.valueOf(localPath.relativize(x))));

        return temp;
    }


    public List<Path> directories(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isDirectory).forEach(temp::add);

        return temp;
    }

    public List<Path> files(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isRegularFile).forEach(x -> {
            if(!String.valueOf(x.getFileName()).equals(fileName)){
                temp.add(x);
            }
        });

        return temp;
    }


    public Client scanPath(Path path) throws IOException {
        list = Files.walk(path);

        return this;
    }

    public String translator(char[] filePath){
        StringBuilder t = new StringBuilder();
        for (char c : filePath) {
            if (c == ' ') t.append("%20");
            else if (c == '!') t.append("%21");
            else if (c == '#') t.append("%23");
            else if (c == '$') t.append("%24");
            else if (c == '%') t.append("%25");
            else if (c == '&') t.append("%26");
            else if (c == '+') t.append("%2B");
            else if (c == ',') t.append("%2C");
            else if (c == ':') t.append("%3A");
            else if (c == ';') t.append("%3B");
            else if (c == '=') t.append("%3D");
            else if (c == '?') t.append("%3F");
            else if (c == '@') t.append("%40");
            else if (c == '[') t.append("%5B");
            else if (c == ']') t.append("%5D");
            else t.append(c);
        }
        return t.toString();
    }
}
