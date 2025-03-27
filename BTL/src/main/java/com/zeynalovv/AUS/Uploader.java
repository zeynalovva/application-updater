package com.zeynalovv.AUS;

import com.jcraft.jsch.*;

import java.nio.file.Path;


public class Uploader {
    private ChannelSftp channelSftp = null;
    private Session jschSession = null;
    public Uploader(Loader load) throws JSchException {
        String[] HOST = load.getServerIP().split(":");
        JSch jsch = new JSch();
        jsch.setKnownHosts("~/.ssh/known_hosts");
        jschSession = jsch.getSession(load.getUsernameFTP(), HOST[0], Integer.parseInt(HOST[1]));
        jschSession.setPassword(load.getPasswordFTP());
        System.out.println("#connect begin.");
        jschSession.connect();
        channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
        channelSftp.connect();
    }

    public void uploadFile(String filePath, String serverPath) throws JSchException, SftpException {
        channelSftp.put(filePath, serverPath);
    }

    public void close(){
        jschSession.disconnect();
        channelSftp.exit();
    }

}
