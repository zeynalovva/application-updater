package com.zeynalovv.Testing;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class main {
    public static void main(String[] args) throws IOException {
        Stream<Path> t = Files.walk(Paths.get("/home/zeynalovv"));
        t.filter(Files::isDirectory).forEach(x -> System.out.println(x));


    }
}
