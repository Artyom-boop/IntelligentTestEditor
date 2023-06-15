package com.project.IntelligentEditorAPI.DTO;

import java.util.List;

public class QuestionDTO {
    private String question;

    private List<AnswerDTO> options;

    public QuestionDTO(String question, List<AnswerDTO> options) {
        this.question = question;
        this.options = options;
    }

    public QuestionDTO() {}


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerDTO> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerDTO> options) {
        this.options = options;
    }
}
