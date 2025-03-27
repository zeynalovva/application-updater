import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class socket {
    public static void main(String[] args) throws JSchException, SftpException, FileNotFoundException {
        ChannelSftp channelSftp = setupJsch();
        channelSftp.connect();
        String serverPath = "/home/zeynalovv/Desktop/ServerApp/Lesson2.pdf";
        String localPath = "/home/zeynalovv/Desktop/TestApp/Lesson2.pdf";
        channelSftp.cd("/home/zeynalovv/Desktop/TestApp");
        channelSftp.mkdir("Test");
        channelSftp.exit();
    }
    public static ChannelSftp setupJsch() throws JSchException, FileNotFoundException, SftpException {
        final String host = "127.0.0.1";
        final int port = 22;
        final String user = "zeynalovv";
        final String password = "Abbas24042006";

        JSch jsch = new JSch();
        jsch.setKnownHosts("~/.ssh/known_hosts");
        Session jschSession = jsch.getSession(user, host, port);
        jschSession.setPassword(password);
        System.out.println("#connect begin.");
        jschSession.connect();
        System.out.println("#connect end.");
        return (ChannelSftp) jschSession.openChannel("sftp");
    }
}
