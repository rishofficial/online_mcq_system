package com.example.demo1.datatypes;

public class Message {
    private final String text;
    private final String senderId;

    public Message(String text, String senderId) {
        this.text = text;
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }
}