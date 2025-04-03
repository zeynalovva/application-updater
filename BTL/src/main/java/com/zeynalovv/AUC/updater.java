package com.zeynalovv.AUC;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
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
    public static void main(String[] args) {
        Path currentDir = Paths.get("/home/zeynalovv/AppUpdater/BTL/src/main/java/com/zeynalovv/AUC");
        //Path currentDir = Paths.get(System.getProperty("user.dir"));

        Loader load = null;
        try {
            load = new Loader(currentDir);
        } catch (IOException e) {
            System.out.println("Could not load the options!");
            return;
        }


        Downloader.download("checksum.json", load);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> directoryTree = null;
        try {
            directoryTree = objectMapper.readValue(new File(String.valueOf(currentDir.resolve("checksum.json"))), new TypeReference<Map<String,String>>(){});
        } catch (IOException e) {System.out.println("Problem occured!");}


        //Delete the folders
        //deleteFolders(load, currentDir, directoryTree);

        ArrayList<String> relativeFilePaths = new ArrayList<>();
        ArrayList<Path> absoluteFilePaths = new ArrayList<>();
        ArrayList<String> relativeDirectoryPath = new ArrayList<>();
        ArrayList<Path> absoluteDirectoryPath = new ArrayList<>();
        try {
            Stream<Path> tree = Files.walk(load.getCurrenDir());
            tree.forEach(x -> {
                String path = String.valueOf(currentDir.relativize(x));
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
        for(String i : directoryTree.keySet()){
            if(i.equals(directoryTree.get(i))){
                Path mkdir = currentDir.resolve(i);
                new File(String.valueOf(mkdir)).mkdirs();
            }
        }

        for(String i : directoryTree.keySet()){
            if(!i.equals(directoryTree.get(i))){
                Path path = currentDir.resolve(Downloader.reTranslator(Path.of(directoryTree.get(i))));
                if(Files.exists(path)){
                    if(!checkFile(path, load).equals(i)){
                        Downloader.download(directoryTree.get(i), load);
                    }
                }
                else{
                    Downloader.download(directoryTree.get(i), load);
                }

            }
        }
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
            Stream<Path> tree = Files.walk(load.getCurrenDir());
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
