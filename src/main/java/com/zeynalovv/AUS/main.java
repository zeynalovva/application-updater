package com.zeynalovv.AUS;


import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class main {

    public static void main(String[] args) {



    }

    /*
    public static void start(Path applicationDir, Path tempDir){
        //Setting up the environment
        Loader load = new Loader(applicationDir, tempDir);
        //Getting all the files into the folder
        ArrayList<String> relativeFilePaths = new ArrayList<>();
        ArrayList<Path> absoluteFilePaths = new ArrayList<>();
        ArrayList<String> relativeDirectoryPath = new ArrayList<>();
        ArrayList<Path> absoluteDirectoryPath = new ArrayList<>();
        try {
            Stream<Path> t = Files.walk(load.getFolderPath());
            t.forEach(x -> {
                String path = String.valueOf(load.getFolderPath().relativize(x));
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
        } catch (IOException e){
            System.out.println("Could not open the folder!");
            return;
        }



        //Hashing the files
        HashMap<String, String> table = new HashMap<>();
        for (int i = 0; i < absoluteFilePaths.size(); i++) {
            File file = new File(String.valueOf(absoluteFilePaths.get(i)));
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] bytes = new byte[(int) file.length()];
                fis.read(bytes);
                StringBuilder byt = new StringBuilder();
                for (byte b : bytes) {
                    byt.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
                }
                table.put(chekcsum(byt.toString()), relativeFilePaths.get(i));
            } catch (FileNotFoundException e) {
                System.out.println("Could not find the file!\nPath: " + absoluteFilePaths.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        /*
        for (String i : relativeDirectoryPath){
            table.put(i, i);
        }




        //Parsing the data into a .json file
        ObjectMapper json = new ObjectMapper();
        ObjectNode root = json.createObjectNode();
        ObjectNode files = json.createObjectNode();
        ObjectNode folders = json.createObjectNode();
        ObjectNode translated = json.createObjectNode();
        for(String i : table.keySet()){
            files.put(i, table.get(i));
        }
        for(String i : relativeDirectoryPath){
            folders.put(i, i);
        }
        for(String i : table.keySet()){
            translated.put(i, translator(table.get(i)));
        }
        root.set("files", files);
        root.set("folders", folders);
        root.set("translated", translated);

        Path checksumPath = load.getFolderPath().resolve("checksum.json");
        absoluteFilePaths.add(checksumPath);
        relativeFilePaths.add("checksum.json");
        try {
            json.writeValue(new File(String.valueOf(checksumPath)), root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Uploading corresponding files and folders into to the remote SFTP server
        try {
            Uploader uploader = new Uploader(load);
            //Creating necessary folders in the server directory
            for(String i : relativeDirectoryPath){
                if(uploader.createFolder(i)) System.out.println("Created: " + i);
            }

            //Uploading the files
            for (int i = 0; i < absoluteFilePaths.size(); i++) {
                String filePath = String.valueOf(absoluteFilePaths.get(i));
                String serverPath = String.valueOf(load.getServerPath().resolve(relativeFilePaths.get(i)));
                System.out.println("Uploading: " + filePath + " - to - " + serverPath);
                uploader.uploadFile(filePath, serverPath);
            }
            uploader.close();
        } catch (JSchException | SftpException e) {
            throw new RuntimeException(e);
        }

    }
    */

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

    static String translator(String filePath){
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < filePath.length();i++) {
            if(filePath.charAt(i) == ' ') t.append("%20");
            else if(filePath.charAt(i) == '!') t.append("%21");
            else if(filePath.charAt(i) == '#') t.append("%23");
            else if(filePath.charAt(i) == '$') t.append("%24");
            else if(filePath.charAt(i) == '%') t.append("%25");
            else if(filePath.charAt(i) == '&') t.append("%26");
            else if(filePath.charAt(i) == '+') t.append("%2B");
            else if(filePath.charAt(i) == ',') t.append("%2C");
            else if(filePath.charAt(i) == ':') t.append("%3A");
            else if(filePath.charAt(i) == ';') t.append("%3B");
            else if(filePath.charAt(i) == '=') t.append("%3D");
            else if(filePath.charAt(i) == '?') t.append("%3F");
            else if(filePath.charAt(i) == '@') t.append("%40");
            else if(filePath.charAt(i) == '[') t.append("%5B");
            else if(filePath.charAt(i) == ']') t.append("%5D");
            else t.append(filePath.charAt(i));
        }
        return t.toString();
    }



}
