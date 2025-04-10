package com.zeynalovv.AUC;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class test {
    public static void main(String[] args) throws IOException {
        Updater update = new Updater(Path.of("/home/zeynalovv/Desktop/TestApp"),
                Path.of("/home/zeynalovv/Desktop/TestApp/checksum.json")).build();
        for (Path path : update.relativeFile) {
            System.out.println(path);
        }
    }
}
