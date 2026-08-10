package com.example.demo1.pages;
import com.example.demo1.datatypes.Info;
import com.example.demo1.datatypes.Message;
import com.example.demo1.datatypes.User;
import com.example.demo1.threads.socketWrap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class ChatWindow {

    public Label User1Id;
    public Label User1Name;
    public Label User2Id;
    public Label User2Name;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<Message> chatListView;
    @FXML
    private TextField messageInputField;
    static private Timeline messageRefreshTimer;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private User currentUser;
    private User anotherUser;
    private static String inboxFile;
    public void handleSendMessage(ActionEvent actionEvent) throws IOException {
        String msg= messageInputField.getText();
        try(socketWrap socket=new socketWrap(Info.ip , Info.port)){
            socket.writeLine("SEND_MESSAGE");
            socket.writeLine(msg);
            socket.writeLine(findInboxFile(currentUser, anotherUser));
            socket.writeLine(currentUser.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        messageInputField.clear();
    }
    public void reRenderListView(){
        if(messageRefreshTimer != null) {
            messageRefreshTimer.stop();
        }
        messageRefreshTimer= new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            try {
                RefreshMessages();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        messageRefreshTimer.setCycleCount(Timeline.INDEFINITE);
        messageRefreshTimer.play();
    }
    public String findInboxFile(User currentUser, User anotherUser) {
        boolean found=false;
        String inboxFile = null;
        try(socketWrap socket=new socketWrap(Info.ip , Info.port)){
            socket.writeLine("FIND_INBOX_FILE");
            socket.writeLine(currentUser.getUserId());
            socket.writeLine(anotherUser.getUserId());
            inboxFile = socket.readLine();
            if(!inboxFile.equals("null") && !inboxFile.isEmpty()) {
                found=true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!found && inboxFile.equals("null")) {
            try(socketWrap socket = new socketWrap(Info.ip, Info.port)) {
                socket.writeLine("CREATE_INBOX_FILE");
                inboxFile = currentUser.getUserId() + "-" + anotherUser.getUserId();
                System.out.println("Creating inbox file: " + inboxFile);
                socket.writeLine(inboxFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return inboxFile;
    }
    public void RefreshMessages() throws Exception {
        try (socketWrap socket = new socketWrap(Info.ip, Info.port)) {
            socket.writeLine("REFRESH");
            socket.writeLine(findInboxFile(currentUser, anotherUser));

            String lastMessage = "";
            if (!messages.isEmpty()) {
                Message last = messages.getLast();
                lastMessage = last.getSenderId() + "_" + last.getText();
            }
            socket.writeLine(lastMessage);

            while (true) {
                String line = socket.readLine();
                if (line == null || line.equals("END")) {
                    break;
                }
                String[] parts = line.split("_");
                if (parts.length < 2) {
                    continue; // Skip invalid messages
                } else {
                    Message newMessage = new Message(parts[1], parts[0]);
                    messages.add(newMessage);
                }
            }
            chatListView.getItems().clear();
            chatListView.getItems().addAll(messages);
            chatListView.scrollTo(messages.size());
        }
    }
    public void loadMessages(String inboxFile) {
        if(!messages.isEmpty()){
            messages.clear();
        }
        if (inboxFile != null) {
            try(socketWrap socket=new socketWrap(Info.ip, Info.port)){
                socket.writeLine("LOAD_MESSAGES");
                socket.writeLine(inboxFile);
                messages.add(new Message(User.getCurrentUser().getUserID(), "WELCOME"));
                while (true) {
                    String line = socket.readLine();
                    if(line.equals("END") || line == null) {
                        break;
                    }
                    String[] parts = line.split("_");
                    if(parts.length < 2) {
                        continue;
                    }else{
                        String senderId = parts[0];
                        String messageText = parts[1];
                        messages.add(new Message(messageText, senderId));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }
    public void initData(User currentUser, User anotherUser){
        this.currentUser = currentUser;
        this.anotherUser = anotherUser;
        inboxFile= findInboxFile(currentUser, anotherUser);
        System.out.println("Initializing chat window for " + currentUser.getName() + " and " + anotherUser.getName());
        System.out.println(inboxFile);
        loadMessages(inboxFile);
        chatListView.setCellFactory(listView -> new MessageCell());
        //chatListView.getStyleClass().add("chat-list-view");
        chatListView.getItems().clear();
        chatListView.getItems().addAll(messages);
        chatListView.scrollTo(messages.size());
        reRenderListView();
        User1Id.setText(currentUser.getUserID());
        User1Name.setText(currentUser.getName().replace("_"," "));
        User2Id.setText(anotherUser.getUserID());
        User2Name.setText(anotherUser.getName().replace("_"," "));
    }

    // for decorating things. will need later to design.
    private class MessageCell extends ListCell<Message>{
        private AnchorPane messagePane = new AnchorPane();
        private Label messageLabel = new Label();

        public MessageCell() {
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(5));
            messagePane.getChildren().add(messageLabel);
        }

        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);
            if (empty || message == null) {
                setGraphic(null);
            } else {
                messageLabel.setText(message.getText());
                messageLabel.getStyleClass().removeAll("sender-bubble", "receiver-bubble");
                AnchorPane.clearConstraints(messageLabel);
                if (message.getSenderId().equals(currentUser.getUserID())) {
                    messageLabel.getStyleClass().add("sender-bubble");
                    AnchorPane.setRightAnchor(messageLabel, 10.0);
                } else {
                    messageLabel.getStyleClass().add("receiver-bubble");
                    AnchorPane.setLeftAnchor(messageLabel, 10.0);
                }
                setGraphic(messagePane);
            }
        }
    }
}
