package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Updater implements Updatable{
    private Stream<Path> list;
    private Path appDir;
    private URI serverURL;
    private String version;

    Updater(Path appDir, Path buildInfo){
        this.appDir = appDir;
        Map<String, String> table = (Map<String, String>) loadSettings(buildInfo);
        for(String i : table.keySet()){
            i.toLowerCase();
            switch (i){
                case "server":
                    serverURL = URI.create(table.get(i));
                    break;
                case "version":
                    version =  table.get(i);
                    break;
            }
        }
    }


    public void download(String fileName, String destination){
        String test = null;
        try{
            Path pth =  appDir.resolve(fileName);
            URI url = serverURL.resolve(pth.toString());
            test = url.toString();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(appDir.resolve(destination)));
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println(url);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection refused!: " + test);

        }
    }



    public String convertBinaryFile(Path path) throws IOException {
        File file = new File(String.valueOf(path));
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        StringBuilder byt = new StringBuilder();

        for (byte b : bytes) {
            byt.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
        }
        return byt.toString();
    }

    public Map<?, ?> loadSettings(Path src) {
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

    public List<Path> relativize(Path path){
        List<Path> temp = new ArrayList<>();
        list.forEach(x -> temp.add(path.relativize(x)));

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


}
