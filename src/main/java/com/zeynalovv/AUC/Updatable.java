package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface Updatable {

    Object scanPath(Path path) throws IOException;

    List<Path> files();

    List<Path> directories();

    List<?> relativize(List<Path> absolutPath);

    default void createDirectory(Path path){
        new File(String.valueOf(path)).mkdirs();
    }

    default String hashOf(Path path) throws NoSuchAlgorithmException, IOException {
        String rawData = convertToBinary(path);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] t = md.digest(rawData.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, t);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    default String convertToBinary(Path path) throws IOException {
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
}
