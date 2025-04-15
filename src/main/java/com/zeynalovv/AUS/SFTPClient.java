package com.zeynalovv.AUS;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SFTPClient extends Application {

    private TextField localPathField;
    private TextField remotePathField;
    private TextField ipAddressField;
    private TextField portField;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fileNameField;
    private Button transferButton;

    // For ignore functionality
    private ListView<String> ignoreListView;
    private ObservableList<String> ignoreItems;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SFTP File Transfer Application");

        // Create the grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Local path section
        Label localPathLabel = new Label("Local Folder Path:");
        grid.add(localPathLabel, 0, 0);

        localPathField = new TextField();
        localPathField.setPrefWidth(300);
        grid.add(localPathField, 1, 0);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> browseForFolder(primaryStage));
        grid.add(browseButton, 2, 0);

        // File name field
        Label fileNameLabel = new Label("File Name:");
        grid.add(fileNameLabel, 0, 1);

        fileNameField = new TextField();
        fileNameField.setPrefWidth(300);
        grid.add(fileNameField, 1, 1);

        // SFTP Connection section
        Label connectionLabel = new Label("SFTP Connection Details:");
        connectionLabel.setStyle("-fx-font-weight: bold");
        grid.add(connectionLabel, 0, 2, 3, 1);

        Label ipAddressLabel = new Label("IP Address:");
        grid.add(ipAddressLabel, 0, 3);

        ipAddressField = new TextField();
        grid.add(ipAddressField, 1, 3);

        Label portLabel = new Label("Port:");
        grid.add(portLabel, 0, 4);

        portField = new TextField("22");
        grid.add(portField, 1, 4);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 5);

        usernameField = new TextField();
        grid.add(usernameField, 1, 5);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 6);

        passwordField = new PasswordField();
        grid.add(passwordField, 1, 6);

        // Remote path section
        Label remotePathLabel = new Label("Remote Folder Path:");
        grid.add(remotePathLabel, 0, 7);

        remotePathField = new TextField();
        grid.add(remotePathField, 1, 7);

        // Ignore files/folders section
        Label ignoreLabel = new Label("Ignore Files/Folders:");
        ignoreLabel.setStyle("-fx-font-weight: bold");
        grid.add(ignoreLabel, 0, 8, 3, 1);

        // Create the ignore list view
        ignoreItems = FXCollections.observableArrayList();
        ignoreListView = new ListView<>(ignoreItems);
        ignoreListView.setPrefHeight(150);
        grid.add(ignoreListView, 0, 9, 3, 1);

        // Buttons for adding and removing ignore items
        HBox ignoreButtonsBox = new HBox(10);

        Button addFileButton = new Button("Add Files");
        addFileButton.setOnAction(e -> addIgnoreFiles(primaryStage));

        Button addFolderButton = new Button("Add Folders");
        addFolderButton.setOnAction(e -> addIgnoreFolders(primaryStage));

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> removeSelectedIgnoreItems());

        ignoreButtonsBox.getChildren().addAll(addFileButton, addFolderButton, removeButton);
        grid.add(ignoreButtonsBox, 0, 10, 3, 1);

        // Transfer button
        transferButton = new Button("Transfer Files");
        transferButton.setOnAction(e -> {
            // You'll connect this to your existing SFTP implementation
            //System.out.println("Local Path: " + localPathField.getText());
            //System.out.println("File Name: " + fileNameField.getText());
            //System.out.println("Remote Path: " + remotePathField.getText());
            //System.out.println("IP Address: " + ipAddressField.getText());
            //System.out.println("Port: " + portField.getText());
            //System.out.println("Username: " + usernameField.getText());
            //System.out.println("Password: " + passwordField.getText());
            Client SFTPClient = new Client(localPathField.getText(), fileNameField.getText(), remotePathField.getText(),
                    ipAddressField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());

            try {
                SFTPClient.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
        grid.add(transferButton, 1, 11);

        // Set the scene
        Scene scene = new Scene(grid, 550, 580);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseForFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Local Folder");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            localPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void addIgnoreFiles(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files to Ignore");

        // Set initial directory to the current local path if it exists
        String localPath = localPathField.getText();
        if (!localPath.isEmpty()) {
            File directory = new File(localPath);
            if (directory.exists() && directory.isDirectory()) {
                fileChooser.setInitialDirectory(directory);
            }
        }

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                String path = file.getAbsolutePath();
                if (!ignoreItems.contains(path)) {
                    ignoreItems.add(path);
                }
            }
        }
    }

    private void addIgnoreFolders(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder to Ignore");

        // Set initial directory to the current local path if it exists
        String localPath = localPathField.getText();
        if (!localPath.isEmpty()) {
            File directory = new File(localPath);
            if (directory.exists() && directory.isDirectory()) {
                directoryChooser.setInitialDirectory(directory);
            }
        }

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String path = selectedDirectory.getAbsolutePath();
            if (!ignoreItems.contains(path)) {
                ignoreItems.add(path);
            }
        }
    }

    private void removeSelectedIgnoreItems() {
        List<String> selectedItems = ignoreListView.getSelectionModel().getSelectedItems();
        ignoreItems.removeAll(selectedItems);
    }

    public static void main(String[] args) {
        launch(args);
    }
}