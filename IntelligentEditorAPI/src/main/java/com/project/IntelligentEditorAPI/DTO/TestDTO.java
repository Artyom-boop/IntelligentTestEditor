package com.project.IntelligentEditorAPI.DTO;

import java.util.List;

public class TestDTO {
    private String title;
    private List<QuestionDTO> questions;

    public TestDTO(Long id, String title, List<QuestionDTO> questions) {
        this.questions = questions;
        this.title = title;
    }

    public TestDTO(List<QuestionDTO> questions) {
        this.questions = questions;
    }
    public TestDTO() {}

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
