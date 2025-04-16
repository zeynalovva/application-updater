package com.zeynalovv.AUS;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.awt.Component;
import java.util.Map;
import javax.swing.SwingUtilities;

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

        Button addItemsButton = new Button("Add Files/Folders to Ignore");
        addItemsButton.setOnAction(e -> addItemsToIgnore());

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> removeSelectedIgnoreItems());

        ignoreButtonsBox.getChildren().addAll(addItemsButton, removeButton);
        grid.add(ignoreButtonsBox, 0, 10, 3, 1);

        // Transfer button
        transferButton = new Button("Transfer Files");
        transferButton.setOnAction(e -> {
            Map<Path, String> temp = new HashMap<>();
            ignoreItems.add("options.json");
            ignoreItems.forEach(x -> {
                Path h = Path.of(x);
                temp.put(h, Files.isDirectory(h) ? "D" : "F");
            });

            Client SFTPClient = new Client(localPathField.getText(), fileNameField.getText(), remotePathField.getText(),
                    ipAddressField.getText(), portField.getText(), usernameField.getText(), passwordField.getText(), temp);
            boolean transferSuccessful = validateInputs();
            try {
                SFTPClient.start();
            } catch (Exception ex) {
                transferSuccessful = false;
            }

            // Show the result in a popup
            showTransferResultPopup(primaryStage, transferSuccessful);
        });
        grid.add(transferButton, 1, 11);

        // Set the scene
        Scene scene = new Scene(grid, 550, 580);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean validateInputs() {
        // This is where you would call your actual SFTP implementation
        // For now, just do basic validation
        String localPath = localPathField.getText();
        String remotePath = remotePathField.getText();
        String ipAddress = ipAddressField.getText();
        String port = portField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple validation - check if required fields are filled
        return !localPath.isEmpty() && !remotePath.isEmpty() &&
                !ipAddress.isEmpty() && !port.isEmpty() &&
                !username.isEmpty() && !password.isEmpty();
    }

    private void showTransferResultPopup(Stage parentStage, boolean successful) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(parentStage);
        popupStage.setTitle("Transfer Status");

        String message;
        String style;

        if (successful) {
            message = "File transfer completed successfully!";
            style = "-fx-text-fill: green; -fx-font-weight: bold;";
        } else {
            message = "File transfer failed. Please check your inputs.";
            style = "-fx-text-fill: red; -fx-font-weight: bold;";
        }

        Label statusLabel = new Label(message);
        statusLabel.setStyle(style);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(statusLabel, closeButton);

        Scene scene = new Scene(layout, 350, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void browseForFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Local Folder");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            localPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void addItemsToIgnore() {
        // We'll use Swing's JFileChooser since JavaFX doesn't natively support
        // selecting both files and folders in the same dialog
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setDialogTitle("Select Files and Folders to Ignore");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // Allow both files and directories

            // Set initial directory to the current local path if it exists
            String localPath = localPathField.getText();
            if (!localPath.isEmpty()) {
                File directory = new File(localPath);
                if (directory.exists() && directory.isDirectory()) {
                    fileChooser.setCurrentDirectory(directory);
                }
            }

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();

                // Add the selected files/folders to our list
                javafx.application.Platform.runLater(() -> {
                    for (File file : selectedFiles) {
                        String path = file.getAbsolutePath();
                        if (!ignoreItems.contains(path)) {
                            ignoreItems.add(path);
                        }
                    }
                });
            }
        });
    }

    private void removeSelectedIgnoreItems() {
        List<String> selectedItems = ignoreListView.getSelectionModel().getSelectedItems();
        ignoreItems.removeAll(selectedItems);
    }

    public static void main(String[] args) {
        launch(args);
    }
}