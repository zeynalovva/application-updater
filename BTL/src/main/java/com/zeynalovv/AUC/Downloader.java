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
        String test = null;
        try{
            Path pth = Paths.get(load.getAppFolder()).resolve(filename);
            URI url = load.getServer().resolve(pth.toString());
            test = url.toString();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(reTranslator(load.getCurrenDir().resolve(filename)));
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println(url);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Connection refused!: " + test);

        }
    }

    public static String reTranslator(Path a){
        String b = String.valueOf(a);
        b = new String(b.replaceAll("%20", " "));
        b = new String(b.replaceAll("%21", "!"));
        b = new String(b.replaceAll("%23", "#"));
        b = new String(b.replaceAll("%24", "$"));
        b = new String(b.replaceAll("%25", "%"));
        b = new String(b.replaceAll("%26", "&"));
        b = new String(b.replaceAll("%2B", "+"));
        b = new String(b.replaceAll("%2C", ","));
        //b.replaceAll("%2F", " ");
        b = new String(b.replaceAll("%3A", ":"));
        b = new String(b.replaceAll("%3B", ";"));
        b = new String(b.replaceAll("%3D", "="));
        b = new String(b.replaceAll("%3F", "?"));
        b = new String(b.replaceAll("%40", "@"));
        b = new String(b.replaceAll("%5B", "["));
        b = new String(b.replaceAll("%5D", "]"));
        return b;
    }
}
