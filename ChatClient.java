package com.example;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class ChatClient extends Application {
    private PrintWriter out;
    private TextArea chatArea;
    private TextField inputField;

    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        inputField = new TextField();

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(10, inputField, sendButton);
        VBox root = new VBox(10, chatArea, inputBox);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("JavaFX Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer("localhost", 5000); // server IP and port
    }

    private void connectToServer(String host, int port) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, port)) {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String msg;
                while ((msg = in.readLine()) != null) {
                    String finalMsg = msg;
                    Platform.runLater(() -> chatArea.appendText(finalMsg + "\n"));
                }
            } catch (IOException e) {
                Platform.runLater(() -> chatArea.appendText("Could not connect to server.\n"));
            }
        }).start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (out != null && !message.isEmpty()) {
            out.println(message);
            chatArea.appendText("Me: " + message + "\n");
            inputField.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}



    

