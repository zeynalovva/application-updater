package com.zeynalovv.AUC;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {

    public static void download(String filename, Loader load, String location){
        Path test = null;
        try{
            Path pth = load.getAppPath().resolve(filename);
            URI url = load.getServer().resolve(filename);
            System.out.println(url);

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(load.getAppPath().resolve(location)));
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println(url);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection refused!: " + test);

        }
    }
}
