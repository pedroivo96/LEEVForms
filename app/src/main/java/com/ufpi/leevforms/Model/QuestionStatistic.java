package com.ufpi.leevforms.Model;

import java.util.ArrayList;

public class QuestionStatistic {
    private Question question;
    private ArrayList<String> questionAnswers;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ArrayList<String> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(ArrayList<String> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}
