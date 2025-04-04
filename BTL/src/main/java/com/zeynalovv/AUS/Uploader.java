package com.zeynalovv.AUS;

import com.jcraft.jsch.*;

public final class Uploader {
    private ChannelSftp channelSftp = null;
    private Session jschSession = null;
    Loader load;
    public Uploader(Loader load) throws JSchException {
        String[] HOST = load.getServerIP().split(":");
        this.load = load;
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

    public boolean createFolder(String directoryPath) throws SftpException {
        channelSftp.cd(String.valueOf(load.getServerPath()));
        boolean check = false;
        int count = 0;
        for (int i = 0; i < directoryPath.length(); i++) {
            if(directoryPath.charAt(i) == ' '){
                count++;
            }
        }
        if(count == directoryPath.length() || directoryPath.isEmpty()) check = true;
        if(!check){
            try{
                channelSftp.mkdir(directoryPath);
                return true;
            }
            catch (SftpException e){
                if(e.id == 4) System.out.println("Directory exists: " + directoryPath);
                return false;
            }
        }
        return false;
    }

    public void close(){
        jschSession.disconnect();
        channelSftp.exit();
    }

}
