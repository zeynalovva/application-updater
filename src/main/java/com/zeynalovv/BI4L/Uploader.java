package com.zeynalovv.BI4L;

import com.jcraft.jsch.*;

import java.nio.file.Path;

public final class Uploader {
    private ChannelSftp channelSftp = null;
    private Session jschSession = null;
    private final String ipAddress, port, username, password, remotePath;

    /**
     * A constructor for the class
     * @param ipAddress IP address of the SFTP server
     * @param port port for the connection
     * @param username username in the SFTP server
     * @param password password for the user
     * @param remotePath the path where files are going to be stored
     */
    public Uploader(String ipAddress, String port, String username, String password, Path remotePath) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.password = password;
        this.remotePath = String.valueOf(remotePath);
    }

    /**
     * Opens a tunnel between the server and <code>Uploader</code>
     * @throws JSchException when an error occurs while opening a connection with the remote SFTP server
     */
    public void openConnection() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("~/.ssh/known_hosts");
        jschSession = jsch.getSession(username, ipAddress, Integer.parseInt(port));
        jschSession.setPassword(password);
        System.out.println("#connect begin.");
        jschSession.connect();
        channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
        channelSftp.connect();
    }

    /**
     * Transmits the file to the given path in the server.
     * @param localFile file which is going to be transmitted
     * @param remoteFile path where the file will go
     * @throws SftpException when an error occurs while sending a data to the SFTP server
     */
    public void uploadFile(String localFile, String remoteFile) throws SftpException {
        channelSftp.put(localFile, remoteFile);
    }

    /**
     * Creates directory in the server.
     * @param directoryPath path for the creation
     * @return status of the operation (if it is newly created, or it did exist)
     * @throws SftpException when an error occurs while sending a command to the SFTP server
     */
    public boolean createFolder(String directoryPath) throws SftpException {
        channelSftp.cd(remotePath);
        boolean check = false;
        int count = 0;
        if(directoryPath.isEmpty()){
            return false;
        }
        try{
            channelSftp.mkdir(directoryPath);
            return true;
        }
        catch (SftpException e){
            if(e.id == 4) System.out.println("Directory exists: " + directoryPath);
            return false;
        }
    }

    /**
     * Closes already opened connection.
     */
    public void close(){
        jschSession.disconnect();
        channelSftp.exit();
    }

}
