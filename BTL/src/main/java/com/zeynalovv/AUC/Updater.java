package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    public Map<?, ?> loadSettings(Path src) throws IOException {
        Map<?, ?> Table = new HashMap<>();
        ObjectMapper jsonFile = new ObjectMapper();
        Table = jsonFile.readValue(new File(String.valueOf(src)), Map.class);

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
