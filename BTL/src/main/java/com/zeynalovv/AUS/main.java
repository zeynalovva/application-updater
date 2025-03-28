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
import java.util.Scanner;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.*;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class main {

    public static void main(String[] args) {
        ProcessBuilder terminal;
        String os = System.getProperty("os.name").toLowerCase();
        Path applicationDir = Paths.get(System.getProperty("user.dir")), currentDir = applicationDir.resolve("BTL"), tempDir = null;
        if (os.contains("win")) {
            String Dir = String.valueOf(currentDir.resolve("settings.exe"));
            terminal = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", Dir);
            tempDir = Paths.get(System.getenv("TEMP"));
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            String Dir = String.valueOf(currentDir.resolve("settings"));
            terminal = new ProcessBuilder("/bin/sh", "-c", "x-terminal-emulator -e " + Dir +
                    " || gnome-terminal -- " + Dir + " || konsole -e " + Dir + " || xfce4-terminal -e " + Dir +
                    " || mate-terminal -e " + Dir + " || lxterminal -e " + Dir + " || alacritty -e " + Dir +
                    " || st -e " + Dir + " || xterm -hold -e " + Dir);
            tempDir = Paths.get("/tmp");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
        try {
            Process terminalProc = terminal.start();
            terminalProc.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        start(applicationDir, tempDir);
    }


    public static void start(Path applicationDir, Path tempDir){
        //Setting up the environment
        Loader load = new Loader();
        Path option = applicationDir.resolve("options.txt");
        try {
            BufferedReader read = new BufferedReader(new FileReader(String.valueOf(option)));
            String line;
            while((line = read.readLine()) != null){
                initialization(line, load);
            }
        } catch (IOException e) {
            try{
                Path tempPath = tempDir.resolve("YGG97ak4994hJ6nok4Pagg.txt");
                BufferedReader read = new BufferedReader(new FileReader(String.valueOf(tempPath)));
                String line;
                while((line = read.readLine()) != null){
                    initialization(line, load);
                }
            } catch (IOException ex) {
                System.out.println("Could not load the options! Check out options.txt file!");
                return;
            }
        }

        //Getting all the files into the folder
        ArrayList<String> relativeFilePaths = new ArrayList<>();
        ArrayList<Path> absoluteFilePaths = new ArrayList<>();
        ArrayList<String> relativeDirectoryPath = new ArrayList<>();
        ArrayList<Path> absoluteDirectoryPath = new ArrayList<>();
        try{
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


        //Parsing the data into a .json file
        ObjectMapper json = new ObjectMapper();
        Path checksumPath = load.getFolderPath().resolve("checksum.json");
        absoluteFilePaths.add(checksumPath);
        relativeFilePaths.add("checksum.json");
        try {
            json.writeValue(new File(String.valueOf(checksumPath)), table);
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


    static void initialization(String line, Loader load){
        String[] t = line.split("=");
        for(options i : options.values()){
            if(i.name().equals(t[0])){
                i.setOptions(load, t[1]);
            }
        }
    }

}
