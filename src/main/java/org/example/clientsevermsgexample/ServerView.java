
package org.example.clientsevermsgexample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private Button Start_Server;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket clientSocket;
    private DataOutputStream dos;


    @FXML
    private void runServer() {

        new Thread(() -> {
            try {

                ServerSocket serverSocket = new ServerSocket(1234);
                updateServer("Server is running and waiting for a client...");


                clientSocket = serverSocket.accept();
                updateServer("Client connected!");

                button_send.setDisable(false);

                listenForMessages();
            } catch (IOException e) {
                updateServer("Error: " + e.getMessage());
            }
        }).start();
    }


    private void listenForMessages() {
        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                while (true) {
                    String message = dis.readUTF();
                    updateTextServer("Client: " + message);
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                updateTextServer("Error receiving message: " + e.getMessage());
            }
        }).start();
    }


    @FXML
    private void sendMessage() {
        String message = tf_message.getText();
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                dos = new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF(message);
                tf_message.clear();
                updateTextServer("Server: " + message);
            } catch (IOException e) {
                updateTextServer("Error sending message: " + e.getMessage());
            }
        } else {
            updateTextServer("No client connected!");
        }
    }


    private void updateServer(String message) {
        Platform.runLater(() -> {
            Label statusLabel = new Label(message);
            vbox_messages.getChildren().add(statusLabel);
        });
    }


    private void updateTextServer(String message) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(message);
            vbox_messages.getChildren().add(msgLabel);
        });
    }
}
