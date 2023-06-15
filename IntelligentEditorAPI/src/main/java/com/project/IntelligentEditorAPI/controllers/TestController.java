package com.project.IntelligentEditorAPI.controllers;

import com.project.IntelligentEditorAPI.model.Question;
import com.project.IntelligentEditorAPI.model.Test;
import com.project.IntelligentEditorAPI.model.TestRequest;
import com.project.IntelligentEditorAPI.repositories.QuestionRepository;
import com.project.IntelligentEditorAPI.repositories.TestRepository;
import com.project.IntelligentEditorAPI.repositories.UserRepository;
import com.project.IntelligentEditorAPI.services.ChatGptService;
import com.project.IntelligentEditorAPI.services.TestService;
import com.project.IntelligentEditorAPI.services.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    ChatGptService chatGptService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TestService testService;

    @Autowired
    ExportService exportService;

    @Autowired
    TestRepository testRepository;

    @GetMapping("/all")
    public String allAccess() {
        return "IntelligentTestEditor - это приложение для создания тестовых заданий" +
                " с использованием ChatGPT.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/get-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test getTest(@RequestParam Long id) {
        return testService.findTestById(id);
    }

    @GetMapping("/test-list")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Test> getTests() {
        return testService.getAllTestsUser();
    }

    @GetMapping("/generate-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test generateTest(@RequestParam String testTopic,
                             @RequestParam Integer numberQuestions,
                             @RequestParam Integer minAnswers,
                             @RequestParam Integer maxAnswers) throws IOException {
        Test test = chatGptService.createTestWithHints(testTopic, numberQuestions, minAnswers, maxAnswers);
        if (test != null) {
            testService.saveTest(test);
        }
        return test;
    }

    @PostMapping("/save-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String saveTest(@RequestBody Test test) {
        testService.updateTest(test);
        return "Test save";
    }

    @PostMapping("/set-question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test setQuestion(@RequestBody TestRequest testRequest) throws IOException {
        Test test = testRequest.getTest();
        String remark = testRequest.getRemark();
        Long questionId = testRequest.getQuestionId();
        Question question = testService.findQuestionById(test, questionId);
        return chatGptService.setQuestionInTest(test, question, remark);
    }

    @GetMapping("/delete-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String deleteTest(@RequestParam Long id) {
        testService.deleteTestById(id);
        return "Тест удалён";
    }

    @GetMapping("/delete-question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test deleteQuestion(@RequestParam Long id) {
        return testService.deleteQuestionById(id);
    }

    @GetMapping("/delete-answer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test deleteAnswer(@RequestParam Long id) {
        return testService.deleteAnswerById(id);
    }

    @GetMapping("/add-question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test addQuestion(@RequestParam Long testId) throws IOException {
        Test test = testService.findTestById(testId);
        chatGptService.addQuestion(test);
        testRepository.save(test);
        return testRepository.findById(test.getId()).orElse(null);
    }

    @GetMapping("/add-answer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Test addAnswer(@RequestParam Long questionId, @RequestParam Boolean correct) throws IOException {
        Question question = testService.findQuestionById(questionId);
        chatGptService.addAnswer(question, correct);
        questionRepository.save(question);
        return testService.findTestById(question.getTest().getId());
    }

    @GetMapping("/export-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String exportTest(@RequestParam Long testId, @RequestParam String flag) {
        Test test = testService.findTestById(testId);
        if (test != null) {
            if (flag.equals("gift"))
                return exportService.exportTestToGift(test);
            if (flag.equals("csv"))
                return exportService.exportTestToCsv(test);
            if (flag.equals("txt"))
                return exportService.exportTestToTxt(test);
        }
        return null;
    }
}