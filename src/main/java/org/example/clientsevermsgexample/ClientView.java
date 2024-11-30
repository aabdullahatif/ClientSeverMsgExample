package org.example.clientsevermsgexample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientView extends Application {
    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket socket1;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(ap_main, 300, 275));
        primaryStage.show();

        connectToServer();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void connectToServer() {
        try {
            socket1 = new Socket("localhost", 1234);
            listenForMessages();
            updateTextClient("Connected to server!");
            button_send.setDisable(false);
        } catch (IOException e) {
            updateTextClient("Error connecting to server: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                dis = new DataInputStream(socket1.getInputStream());
                while (true) {
                    String message = dis.readUTF();
                    updateTextClient("Server: " + message);
                }
            } catch (IOException e) {
                updateTextClient("Error receiving message: " + e.getMessage());
                closeSocket();
            }
        }).start();
    }

    public void sendMessage() {
        String message = tf_message.getText();
        if (socket1 != null && socket1.isConnected()) {
            try {
                dos = new DataOutputStream(socket1.getOutputStream());
                dos.writeUTF(message);
                updateTextClient("Client: " + message);
                tf_message.clear();
            } catch (IOException e) {
                updateTextClient("Error sending message: " + e.getMessage());
            }
        } else {
            updateTextClient("Socket is not connected or is closed.");
        }
    }

    private void updateTextClient(String message) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(message);
            vbox_messages.getChildren().add(msgLabel);
        });
    }

    private void closeSocket() {
        try {
            if (socket1 != null && !socket1.isClosed()) {
                socket1.close();
                updateTextClient("Disconnected from server.");
            }
        } catch (IOException e) {
            updateTextClient("Error closing socket: " + e.getMessage());
        }
    }

    public void disconnectFromServer() {
        closeSocket();
    }
}
