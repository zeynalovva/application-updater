package com.zeynalovv.BI4L;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeynalovv.BI4L.ExceptionLibrary.*;

import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

public final class Updater implements Updatable{
    private Stream<Path> list;
    private Path appDir, buildInfo;
    private URI serverURL;
    private String version;
    private List<String> relativeFile, relativeDir;
    private boolean noDelete = false, noNewFile = false, noChange = false;
    public Map<String, String> filesJson, foldersJson, translatedJson, ignoreJson;

    public Updater(Path appDir, Path buildInfo){
        this.appDir = appDir;
        this.buildInfo = buildInfo;
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
        ignoreJson.put(String.valueOf(appDir.relativize(buildInfo)), "F");
        if(!noDelete){
            for(String file : relativeFile){
                if(!filesJson.containsValue(file) && !ignoreJson.containsKey(file)){
                    System.out.println(file);
                    Files.delete(appDir.resolve(file));
                }
            }
            for(String dir : relativeDir){
                if(!foldersJson.containsKey(dir) && !ignoreJson.containsKey(dir) && !dir.equals("")){
                    System.out.println(dir);
                    delete(appDir.resolve(dir));
                }
            }
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
                case "ignore" -> ignoreJson = (HashMap<String, String>) entry.getValue();
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

    public List<String> relativize(List<Path> absolutPath){
        List<String> temp = new ArrayList<>();
        absolutPath.forEach(x -> temp.add(String.valueOf(appDir.relativize(x))));

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

    public void delete(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)){
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
