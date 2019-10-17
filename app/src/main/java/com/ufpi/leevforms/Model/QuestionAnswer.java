package com.ufpi.leevforms.Model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class QuestionAnswer {
    private String idQuestion;
    private String questionDescription;
    private ArrayList<String> description;

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}
