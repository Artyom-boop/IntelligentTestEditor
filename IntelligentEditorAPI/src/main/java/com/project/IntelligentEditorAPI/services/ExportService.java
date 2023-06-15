package com.project.IntelligentEditorAPI.services;

import com.project.IntelligentEditorAPI.model.Answer;
import com.project.IntelligentEditorAPI.model.Question;
import com.project.IntelligentEditorAPI.model.Test;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

    public String exportTestToGift(Test test) {
        StringBuilder result = new StringBuilder();
        for (Question question: test.getQuestions()) {
            result.append(exportQuestionToGift(question));
        }
        return result.toString();
    }

    public String exportQuestionToGift(Question question) {
        StringBuilder result = new StringBuilder(question.getQuestion() + " {\n");
        for (Answer answer: question.getOptions()) {
            result.append(exportAnswerToGift(answer));
        }
        result.append("}\n\n");
        return result.toString();
    }

    public String exportAnswerToGift(Answer answer) {
        if (answer.getCorrect()) {
            return "=" + answer.getText() + " # " + answer.getHint() + "\n";
        }
        return "~" + answer.getText() + " # " + answer.getHint()  + "\n";
    }

    public String exportTestToCsv(Test test) {
        StringBuilder result = new StringBuilder();
        for (Question question: test.getQuestions()) {
            result.append(exportQuestionToCsv(question));
        }
        return result.toString();
    }

    public String exportQuestionToCsv(Question question) {
        StringBuilder result = new StringBuilder("text;" + question.getQuestion() + ";-\n");
        for (Answer answer: question.getOptions()) {
            result.append(exportAnswerToCsv(answer));
        }
        return result.toString();
    }

    public String exportAnswerToCsv(Answer answer) {
        if (answer.getCorrect()) {
            return "option;" + answer.getText() + ";y\n";
        }
        return "option;" + answer.getText() + ";n\n";
    }

    public String exportTestToTxt(Test test) {
        StringBuilder result = new StringBuilder();
        int number = 1;
        for (Question question: test.getQuestions()) {
            result.append(number).append(".").append(exportQuestionToTxt(question));
            number++;
        }
        return result.toString();
    }

    public String exportQuestionToTxt(Question question) {
        StringBuilder result = new StringBuilder(" " + question.getQuestion() + "\n");
        int number = 1;
        for (Answer answer: question.getOptions()) {
            result.append("\t").append(number).append(".").append(exportAnswerToTxt(answer));
            number++;
        }
        return result.append('\n').toString();
    }

    public String exportAnswerToTxt(Answer answer) {
        return " " + answer.getText() + "\n";
    }
}
