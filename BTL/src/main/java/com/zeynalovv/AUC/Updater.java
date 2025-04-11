package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeynalovv.AUC.ExceptionAUC.*;

import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Updater implements Updatable{
    private Stream<Path> list;
    private Path appDir;
    private URI serverURL;
    private String version;
    private List<Path> relativeFile, relativeDir;
    private boolean noDelete = false, noNewFile = false, noChange = false;
    public Map<String, String> filesJson, foldersJson, translatedJson;

    public Updater(Path appDir, Path buildInfo){
        this.appDir = appDir;
        Map<String, String> table = (Map<String, String>) this.loadSettings(buildInfo);
        for(String i : table.keySet()){
            String lowered = i.toLowerCase();
            switch (lowered){
                case "server":
                    serverURL = URI.create(table.get(lowered));
                    break;
                case "version":
                    version =  table.get(lowered);
                    break;
            }
        }
        if(serverURL == null || version == null){
            throw new LoaderNullException();
        }
    }



    public void start() throws NoSuchAlgorithmException, IOException{
        for(String i : foldersJson.keySet()){
            createDirectory(appDir.resolve(foldersJson.get(i)));
        }

        if(!noNewFile){
            for(String i : filesJson.keySet()){
                Path path = appDir.resolve(filesJson.get(i));
                if(!Files.exists(path)){
                    download(translatedJson.get(i), filesJson.get(i));
                }
            }
        }

        if(!noChange){
            for(String i : filesJson.keySet()){
                Path path = appDir.resolve(filesJson.get(i));
                if(Files.exists(path) && !isSameFile(path, i)){
                    download(translatedJson.get(i), filesJson.get(i));
                }
            }
        }

    }

    public Updater build() throws IOException {
        this.relativeFile = relativize(this.scanPath(appDir).files());
        this.relativeDir = relativize(this.scanPath(appDir).directories());

        return this;
    }

    public void download(String fileName, String destination){
        String reason = null;
        try{
            URI url = serverURL.resolve(fileName);
            reason = url.toString();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(appDir.resolve(destination)));
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println(url);
        }
        catch (Exception e){
            throw new DownloadException(reason);
        }
    }


    public void readJson(String checksumFile) throws IOException {
        download(checksumFile, checksumFile);
        ObjectMapper readFile = new ObjectMapper();
        Map<String, Object> temp = readFile.readValue(new File(String.valueOf(appDir.resolve(checksumFile))), Map.class);
        for(Map.Entry<String, Object> entry : temp.entrySet()){
            switch (entry.getKey()) {
                case "files" -> filesJson = (HashMap<String, String>) entry.getValue();
                case "folders" -> foldersJson = (HashMap<String, String>) entry.getValue();
                case "translated" -> translatedJson = (HashMap<String, String>) entry.getValue();
            }
        }
    }


    public boolean isSameFile(Path pth1, String hashValue) throws NoSuchAlgorithmException, IOException {
        return hashOf(pth1).equals(hashValue);
    }

    private Map<?, ?> loadSettings(Path src) {
        Map<?, ?> Table = new HashMap<>();
        ObjectMapper jsonFile = new ObjectMapper();
        try{
            Table = jsonFile.readValue(new File(String.valueOf(appDir.resolve(src))), Map.class);
        }
        catch (IOException e){
            System.out.println("The path given for options file could not be read!!");
            return null;
        }
        return Table;
    }

    public List<Path> relativize(List<Path> absolutPath){
        List<Path> temp = new ArrayList<>();
        absolutPath.forEach(x -> temp.add(appDir.relativize(x)));

        return temp;
    }

    public List<Path> directories(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isDirectory).forEach(x -> temp.add(x));

        return temp;
    }

    public List<Path> files(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isRegularFile).forEach(x -> temp.add(x));

        return temp;
    }

    public List<Path> all(){
        List<Path> temp = new ArrayList<>();
        temp.forEach(x -> temp.add(x));

        return temp;
    }

    public Updater scanPath(Path path) throws IOException {
        list = Files.walk(path);

        return this;
    }

    public Updater noDelete(){
        noDelete = true;
        return this;
    }

    public Updater noNewFile(){
        noNewFile = true;
        return this;
    }

    public Updater noChange(){
        noChange = true;
        return this;
    }
}
