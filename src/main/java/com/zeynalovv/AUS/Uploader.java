package com.zeynalovv.AUS;

import com.jcraft.jsch.*;

import java.nio.file.Path;

public final class Uploader {
    private ChannelSftp channelSftp = null;
    private Session jschSession = null;
    private final String ipAddress, port, username, password, remotePath;

    public Uploader(String ipAddress, String port, String username, String password, Path remotePath) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.password = password;
        this.remotePath = String.valueOf(remotePath);
    }

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

    public void uploadFile(String localFile, String remoteFile) throws SftpException {
        channelSftp.put(localFile, remoteFile);
    }

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

    public void close(){
        jschSession.disconnect();
        channelSftp.exit();
    }

}
