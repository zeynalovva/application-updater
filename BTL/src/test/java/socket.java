import com.jcraft.jsch.*;

public class socket {
    public static void main(String[] args) throws JSchException {
        connect();
    }
    public static ChannelSftp connect() throws JSchException {
        final String FTP_SERVER = "ftp.dlptest.com";
        final int FTP_PORT = 21;
        final String FTP_USER = "dlpuser";
        final String FTP_PASSWORD = "rNrKYTX9g7z3RgJRmxWuGHbeu";
        JSch jsch = new JSch();
        jsch.setKnownHosts("../.ssh/known_hosts");
        Session jschSession = jsch.getSession(FTP_USER, FTP_SERVER, FTP_PORT);
        jschSession.setPassword(FTP_PASSWORD);
        System.out.println("#connect begin.");
        jschSession.connect();
        System.out.println("#connect end.");
        return (ChannelSftp) jschSession.openChannel("sftp");


    }
}
