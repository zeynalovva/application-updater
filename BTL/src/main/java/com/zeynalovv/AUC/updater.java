package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

public class updater {

    public static Loader init(){
        Path currentDir = Paths.get("/home/zeynalovv/AppUpdater/BTL/src/main/java/com/zeynalovv/AUC");
        //Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path optionPath = currentDir.resolve("options.txt");
        HashMap<String, String> options = new HashMap<>();
        try{
            BufferedReader read = new BufferedReader(new FileReader(String.valueOf(optionPath)));
            String line;
            while((line = read.readLine()) != null){
                String[] t = line.split("=");
                switch (t[0]){
                    case "SERVER":
                        options.put(t[0], t[1]);
                        break;
                    case "VERSION":
                        options.put(t[0], t[1]);
                        break;
                }
            }
        } catch (IOException e){
            System.out.println("Could not load the options!");
            return null;
        }
        return new Loader.Builder(currentDir).server(options.get("SERVER")).build();

    }

    public static void main(String[] args) {
        Loader load = init();
        System.out.println(load.getAppPath());

        Downloader.download("checksum.json", load, "checksum.json");
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> files = new HashMap<>(), folders = new HashMap<>(), translated = new HashMap<>();
        Map<String, Object> directoryTree = null;
        try {
            directoryTree = objectMapper.readValue(new File(String.valueOf(load.getAppPath().resolve("checksum.json"))), Map.class);
            for(Map.Entry<String, Object> entry : directoryTree.entrySet()){
                if(entry.getKey().equals("files")){
                    files = (HashMap<String, String>) entry.getValue();
                }
                else if(entry.getKey().equals("folders")){
                    folders = (HashMap<String, String>) entry.getValue();
                }
                else if(entry.getKey().equals("translated")){
                    translated = (HashMap<String, String>) entry.getValue();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(translated);


        ArrayList<String> relativeFilePaths = new ArrayList<>();
        ArrayList<Path> absoluteFilePaths = new ArrayList<>();
        ArrayList<String> relativeDirectoryPath = new ArrayList<>();
        ArrayList<Path> absoluteDirectoryPath = new ArrayList<>();
        try {
            Stream<Path> tree = Files.walk(load.getAppPath());
            tree.forEach(x -> {
                String path = String.valueOf(load.getAppPath().relativize(x));
                if(!path.equals("checksum.json")  && !path.equals("options.txt")){
                    if(Files.isRegularFile(x)) {
                        relativeFilePaths.add(path);
                        absoluteFilePaths.add(x);
                    }
                    else if(Files.isDirectory(x)){
                        relativeDirectoryPath.add(path);
                        absoluteDirectoryPath.add(x);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Create folders given by the json
        for(String i : folders.keySet()){
            Path mkdir = load.getAppPath().resolve(folders.get(i));
            new File(String.valueOf(mkdir)).mkdirs();
        }


        //Check if the files have been altered
        for(String i : files.keySet()){
            Path path = load.getAppPath().resolve(Path.of(files.get(i)));
            if(Files.exists(path)){
                if(!checkFile(path, load).equals(i)){
                    Downloader.download(translated.get(i), load, files.get(i));
                }
            }
            else {
                Downloader.download(translated.get(i), load, files.get(i));
            }
        }*/
    }



    static String checkFile(Path path, Loader load){
        File file = new File(String.valueOf(path));
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            StringBuilder byt = new StringBuilder();
            for (byte b : bytes) {
                byt.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
            }
            return chekcsum(byt.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the file!\nPath: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "0";
    }

    public static String chekcsum(String val){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] t = md.digest(val.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, t);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 64)
            {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }



    @Deprecated
    static void deleteFolders(Loader load, Path currentDir, Map<String, String> directoryTree){
        try{
            Stream<Path> tree = Files.walk(load.getAppPath());
            Vector<Path> tempList = new Vector<>();
            tree.forEach(x -> {
                Path relative = currentDir.relativize(x);
                tempList.add(relative);
            });
            for(Path i : tempList){
                if(!directoryTree.containsValue(String.valueOf(i)) && !String.valueOf(i).equals("options.txt")  && !String.valueOf(i).equals("checksum.json")){
                    System.out.println(i);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
