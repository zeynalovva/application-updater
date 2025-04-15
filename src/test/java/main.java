import com.zeynalovv.AUC.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Updater update = new Updater(Path.of("/home/zeynalovv/Desktop/TestApp/"),
                Path.of("/home/zeynalovv/Desktop/TestApp/options.json")).build();
        update.readJson("checksum.json");
        update.start();
    }
}
