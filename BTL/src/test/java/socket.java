import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;




public class socket {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        URI url = new URI("http://127.0.0.1");
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.toURL().openStream());
        FileOutputStream fileOutputStream = new FileOutputStream("test.pdf");
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);


        url = url.resolve("checksum.json");
        System.out.println(url);

    }


}
