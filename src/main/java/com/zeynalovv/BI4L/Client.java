package com.zeynalovv.BI4L;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Client implements Updatable{
    private final Path localPath, remotePath;
    private final String port, username, password;
    private final String fileName, ipAddress;
    private Map<Path, String> ignoreItems;
    private Stream<Path> list;
    private List<Path> absoluteFile;
    private List<String> relativeDir;
    private Map<String, String> hashTable;


    /**
     * A constructor for the class
     * @author zeynalovvabbas
     * @param localPath the path which the original and new version of the files are stored.
     * @param fileName a custom name for the checksum file.
     * @param remotePath is the path in which the new release of the application is going to be stored in the server.
     * @param ipAddress the library uses the SFTP protocol, so the IP address is supplied to <code>Client</code>.
     * @param port port
     * @param username username
     * @param password the port, username, and the password for the SFTP connection.
     * @param ignoreItems is a <code>Map</code> in which the path is stored as a key, and the type of the path,
     *                   whether it is a directory or a file is kept as a value which are going to be
     *                   ignored in the process of updating. See GitHub repo for more.
     *
     */

    public Client(String localPath, String fileName, String remotePath, String ipAddress,
                  String port, String username, String password, Map<Path, String> ignoreItems){

        this.localPath = Path.of(localPath);
        this.ipAddress = ipAddress;
        this.port = port;
        this.password = password;
        this.remotePath = Path.of(remotePath);
        this.username = username;
        this.fileName = fileName;
        this.ignoreItems = ignoreItems;
    }

    /**
     * Scans the <code>localPath</code>, which refers to the directory containing the new but unreleased
     * version of the application on the developer's computer. The absolute paths of all files and directories
     * found are stored in a list called <code>absoluteFile</code>. Then, only the folder paths are selected,
     * converted to relative paths, and stored in another list called <code>relativeDir</code>.
     * <p>
     * <code>hashTable</code> - is a <code>Map</code>, containing hash value(checksum) of each file in the key,
     * and their relative paths in the value section.
     *
     * @throws IOException as parser throws IOException when writing the data into a JSON while, so does <code>start()
     * </code>
     * @throws NoSuchAlgorithmException when hashing function receives a wrong hashing algorithm
     * @throws SftpException when an error occurs in <code>Uploader</code>
     * @throws JSchException when an error occurs in <code>Uploader</code>
     */

    public void start() throws IOException, NoSuchAlgorithmException, SftpException, JSchException {
        this.absoluteFile = this.scanPath(localPath).files();
        this.relativeDir = relativize(this.scanPath(localPath).directories());

        hashTable = new HashMap<>();

        for(Path x : absoluteFile){
            hashTable.put(hashOf(x), String.valueOf(localPath.relativize(x)));
        }
        parser();
        upload();
    }

    /**
     * Parses the context of <code>hashTable</code> to a JSON file. However, the data is seperated into several
     * clusters in the JSON file.
     * <p>
     * Those which are going to be ignored - <code>ignoreList</code>, files - <code>files</code>,
     *  directories - <code>folders</code>, converted version of files to percent encoding - <code>translated</code>.
     *
     * @throws IOException when an error occurs with the checksum file
     */

    public void parser() throws IOException {
        ObjectMapper json = new ObjectMapper();
        ObjectNode root = json.createObjectNode();
        ObjectNode files = json.createObjectNode();
        ObjectNode folders = json.createObjectNode();
        ObjectNode translated = json.createObjectNode();
        ObjectNode ignoreList = json.createObjectNode();

        for(String i : hashTable.keySet()){
            files.put(i, hashTable.get(i));
            translated.put(i, URLEncoding(hashTable.get(i).toCharArray()));
        }

        for(String i : relativeDir){
            if(!i.equals("")) folders.put(i, "D");
        }


        for(Path i : ignoreItems.keySet()){
            if(ignoreItems.get(i) == "D") ignoreList.put(String.valueOf(localPath.relativize(i)), "D");
            else ignoreList.put(String.valueOf(localPath.relativize(i)), "F");
        }

        root.set("ignore", ignoreList);
        root.set("files", files);
        root.set("folders", folders);
        root.set("translated", translated);

        json.writeValue(new File(String.valueOf(localPath.resolve(fileName))), root);
        absoluteFile.add(localPath.resolve(fileName));

    }

    /**
     * Uploads all files and create folders in the remote server by using the <code>Uploader</code> class.
     *
     * @throws JSchException when an error occurs in <code>Uploader</code>
     * @throws SftpException when an error occurs in <code>Uploader</code>
     */

    public void upload() throws JSchException, SftpException {
        Uploader uploader = new Uploader(ipAddress, port, username, password, remotePath);
        uploader.openConnection();
        for(String i : relativeDir){
            if(uploader.createFolder(i))
                System.out.println("Created: " + i);
        }

        for (Path i : absoluteFile) {
            String filePath = String.valueOf(i);
            String serverPath = String.valueOf(remotePath.resolve(localPath.relativize(i)));
            System.out.println("Uploading: " + filePath + " - to - " + serverPath);
            uploader.uploadFile(filePath, serverPath);
        }
        uploader.close();
    }


    /**
     * Relativizes a whole list of paths with <code>localPath</code>.
     * @param absolutePath is a list of paths where only absolute version of them are kept
     * @return a new list of relative paths
     */

    public List<String> relativize(List<Path> absolutePath){
        List<String> temp = new ArrayList<>();
        absolutePath.forEach(x -> temp.add(String.valueOf(localPath.relativize(x))));

        return temp;
    }

    /**
     * Filters only directories out of all paths.
     * @return a new list where only directories are kept
     */
    public List<Path> directories(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isDirectory).forEach(temp::add);

        return temp;
    }

    /**
     * Filters only files out of all paths, but does not include the checksum file for some reason.
     * @return a new list where only files are kept
     */
    public List<Path> files(){
        List<Path> temp = new ArrayList<>();
        list.filter(Files::isRegularFile).forEach(x -> {
            if(!String.valueOf(x.getFileName()).equals(fileName)){
                temp.add(x);
            }
        });

        return temp;
    }

    /**
     *
     * @param path is used to scan everything in it
     * @return <code>Client</code>, so that it can be used with the method chaining to get files
     * or directories
     * @throws IOException when the given path could not be found
     */
    public Client scanPath(Path path) throws IOException {
        list = Files.walk(path);

        return this;
    }

    /**
     * Encodes the file path. Since URLs cannot contain spaces and other characters, they must be percent encoded.
     * @param filePath char array of the path
     * @return encoded path
     */
    public String URLEncoding(char[] filePath){
        StringBuilder t = new StringBuilder();
        for (char c : filePath) {
            if (c == ' ') t.append("%20");
            else if (c == '!') t.append("%21");
            else if (c == '#') t.append("%23");
            else if (c == '$') t.append("%24");
            else if (c == '%') t.append("%25");
            else if (c == '&') t.append("%26");
            else if (c == '+') t.append("%2B");
            else if (c == ',') t.append("%2C");
            else if (c == ':') t.append("%3A");
            else if (c == ';') t.append("%3B");
            else if (c == '=') t.append("%3D");
            else if (c == '?') t.append("%3F");
            else if (c == '@') t.append("%40");
            else if (c == '[') t.append("%5B");
            else if (c == ']') t.append("%5D");
            else t.append(c);
        }
        return t.toString();
    }
}
