package com.example.demo1.datatypes;

import java.util.List;

public class Question {
    private Option option1;
    private Option option2;
    private Option option3;
    private Option option4;
    private String questionText;

    public Question(String questionText, List<Option> options) {
        this.questionText = questionText;
        this.option1 = options.get(0);
        this.option2 = options.get(1);
        this.option3 = options.get(2);
        this.option4 = options.get(3);
    }
    public String getQuestionText() {
        return questionText;
    }
    public Option getOption1() {
        return option1;
    }
    public Option getOption2() {
        return option2;
    }
    public Option getOption3() {
        return option3;
    }
    public Option getOption4() {
        return option4;
    }

}
