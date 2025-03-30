package com.zeynalovv.AUC;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class uploader {
    public static void main(String[] args) {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Loader load = new Loader(currentDir);
        Downloader connection = new Downloader();
        try {
            connection.download("checksum.json", load);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(new File(String.valueOf(currentDir.resolve("checksum.json"))), new TypeReference<Map<String,String>>(){});
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }




    }
}
