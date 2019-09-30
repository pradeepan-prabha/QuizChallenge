package com.hitech.quizchallenge.pojo;

public class QuizQuestionDetailsPojo {
    private String question;
    private String questionNo;
    private String ans;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;

    public QuizQuestionDetailsPojo(String selected) {
        this.selected = selected;
    }

    private String selected;

    public QuizQuestionDetailsPojo(String question, String questionNo, String ans, String choiceA, String choiceB, String choiceC, String choiceD) {
        this.question = question;
        this.questionNo = questionNo;
        this.ans = ans;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
