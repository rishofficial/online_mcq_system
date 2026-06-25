package com.example.demo1.datatypes;

public class Option {
    private String text;
    private boolean isCorrect;

    public Option(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public String getText() {
        return text;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
}
