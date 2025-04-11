package com.zeynalovv.AUC;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class test {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Updater update = new Updater(Path.of("/home/zeynalovv/AppUpdater/BTL/src/main/java/com/zeynalovv/AUC"),
                Path.of("/home/zeynalovv/AppUpdater/BTL/src/main/java/com/zeynalovv/AUC/options.json")).build();
        update.readJson("checksum.json");
        update.noNewFile().start();
    }
}
