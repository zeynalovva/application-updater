import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;




public class socket {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder;
        String os = System.getProperty("os.name").toLowerCase();
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        currentDir = currentDir.resolve("BTL");
        if (os.contains("win")) {
            String Dir = String.valueOf(currentDir.resolve("settings.exe"));
            System.out.println(Dir);
            processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", Dir);
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            String Dir = String.valueOf(currentDir.resolve("settings"));
            System.out.println(Dir);
            processBuilder = new ProcessBuilder("/bin/sh", "-c", "x-terminal-emulator -e " + Dir +
                    " || gnome-terminal -- " + Dir + " || konsole -e " + Dir + " || xfce4-terminal -e " + Dir +
                    " || mate-terminal -e " + Dir + " || lxterminal -e " + Dir + " || alacritty -e " + Dir +
                    " || st -e " + Dir + " || xterm -hold -e " + Dir);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
        processBuilder.start();


    }


}
