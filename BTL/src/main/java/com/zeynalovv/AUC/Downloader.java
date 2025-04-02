package com.zeynalovv.AUC;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {

    public static void download(String filename, Loader load){
        try{
            Path pth = Paths.get(load.getAppFolder()).resolve(filename);
            URI url = load.getServer().resolve(pth.toString());
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(load.getCurrenDir().resolve(filename)));
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println(url);
        }
        catch (IOException e){
            System.out.println("Connection refused!");

        }
    }
}
