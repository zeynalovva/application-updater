package com.zeynalovv.AUC;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface Updatable {

    default String hashing(String rawData) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] t = md.digest(rawData.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, t);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    default void createDirectory(Path path){
        new File(String.valueOf(path)).mkdirs();
    }

    Map<?, ?> loadSettings(Path src) throws IOException;

    Updater scanPath(Path path) throws IOException;

    List<Path> files();

    List<Path> directories();

    List<Path> all();
}
