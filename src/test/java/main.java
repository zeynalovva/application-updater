import com.zeynalovv.AUC.*;
import com.zeynalovv.AUS.Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Updater update = new Updater(Path.of("/home/zeynalovv/Desktop/TestApp/"),
                Path.of("/home/zeynalovv/Desktop/TestApp/options.json")).build();
        update.readJson("checksum.json");
        update.start();


    }
}
