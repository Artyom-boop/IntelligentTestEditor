package com.project.IntelligentEditorAPI.DTO;

public class AnswerDTO {

    private String text;

    private String hint;

    private Boolean correct;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean isCorrect) {
        this.correct = isCorrect;
    }
}
