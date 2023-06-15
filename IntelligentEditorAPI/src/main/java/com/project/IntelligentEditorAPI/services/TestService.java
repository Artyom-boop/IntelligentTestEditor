package com.project.IntelligentEditorAPI.services;

import com.project.IntelligentEditorAPI.DTO.AnswerDTO;
import com.project.IntelligentEditorAPI.DTO.QuestionDTO;
import com.project.IntelligentEditorAPI.DTO.TestDTO;
import com.project.IntelligentEditorAPI.model.Answer;
import com.project.IntelligentEditorAPI.model.Question;
import com.project.IntelligentEditorAPI.model.Test;
import com.project.IntelligentEditorAPI.model.User;
import com.project.IntelligentEditorAPI.repositories.AnswerRepository;
import com.project.IntelligentEditorAPI.repositories.QuestionRepository;
import com.project.IntelligentEditorAPI.repositories.TestRepository;
import com.project.IntelligentEditorAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TestService {

    @Autowired
    TestRepository testRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public void saveTest(Test test) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);
        test.setUser(currentUser);
        testRepository.save(test);
    }

    public List<Test> getAllTestsUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (currentUser != null)
            return currentUser.getTests();
        return null;
    }

    public void updateTest(Test currentTest) {
        Test test = testRepository.findById(currentTest.getId()).orElse(null);
        if (test != null) {
            test.setTitle(currentTest.getTitle());
            List<Question> questions = test.getQuestions();
            List<Question> currentQuestions = currentTest.getQuestions();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                Question currentQuestion = currentQuestions.get(i);
                question.setQuestion(currentQuestion.getQuestion());
                List<Answer> options = question.getOptions();
                List<Answer> currentOptions = currentQuestion.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    Answer option = options.get(j);
                    Answer currentOption = currentOptions.get(j);
                    option.setText(currentOption.getText());
                    option.setHint(currentOption.getHint());
                    option.setCorrect(currentOption.getCorrect());
                }
            }
            testRepository.save(test);
        }
    }

    public Test findTestById(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    public Question findQuestionById(Long id) { return questionRepository.findById(id).orElse(null); }

    public void deleteTestById(Long id) {
        Test test = findTestById(id);
        testRepository.delete(test);
    }

    public Test deleteQuestionById(Long id) {
        Question currentQuestion = questionRepository.findById(id).orElse(null);
        if (currentQuestion != null) {
            Test test = currentQuestion.getTest();
            for (int i = 0; i < test.getQuestions().size(); i++) {
                if (Objects.equals(test.getQuestions().get(i).getId(), currentQuestion.getId())) {
                    test.getQuestions().remove(i);
                    break;
                }
            }
            testRepository.save(test);
            questionRepository.delete(currentQuestion);
            return test;
        }
        return null;
    }

    public Test convertTestDtoToTest(TestDTO testDTO) {
        Test test = new Test();
        test.setTitle(testDTO.getTitle());
        test.setQuestions(new ArrayList<>());

        for (QuestionDTO questionDTO: testDTO.getQuestions()) {
            Question question = convertQuestionDtoToQuestion(questionDTO);
            question.setTest(test);
            test.getQuestions().add(question);
        }
        return test;
    }

    public Question convertQuestionDtoToQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setOptions(new ArrayList<>());
        question.setQuestion(questionDTO.getQuestion());
        for (AnswerDTO option: questionDTO.getOptions()) {
            Answer answer = convertAnswerDtoToAnswer(option);
            answer.setQuestion(question);
            question.getOptions().add(answer);
        }
        return question;
    }

    public Answer convertAnswerDtoToAnswer(AnswerDTO answerDTO) {
        Answer answer = new Answer();
        answer.setText(answerDTO.getText());
        answer.setHint(answerDTO.getHint());
        answer.setCorrect(answerDTO.getCorrect());
        return answer;
    }

    public TestDTO testToTestDto(Test test) {
        TestDTO testDTO = new TestDTO();
        testDTO.setTitle(test.getTitle());
        testDTO.setQuestions(new ArrayList<>());
        for (Question question: test.getQuestions()) {
            testDTO.getQuestions().add(questionToQuestionDto(question));
        }

        return testDTO;
    }

    public QuestionDTO questionToQuestionDto(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setOptions(new ArrayList<>());
        questionDTO.setQuestion(question.getQuestion());
        for (Answer option: question.getOptions()) {
            questionDTO.getOptions().add(answerToAnswerDTO(option));
        }
        return questionDTO;
    }

    private AnswerDTO answerToAnswerDTO(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setText(answer.getText());
        answerDTO.setHint(answer.getHint());
        answerDTO.setCorrect(answer.getCorrect());
        return answerDTO;
    }


    public Question findQuestionById(Test test, Long questionId) {
        for (Question question: test.getQuestions()) {
            if (question.getId().equals(questionId)) {
                return question;
            }
        }
        return null;
    }

    public Test deleteAnswerById(Long id) {
        Answer currentAnswer = answerRepository.findById(id).orElse(null);
        if (currentAnswer != null) {
            Question question = currentAnswer.getQuestion();
            Test test = question.getTest();
            for (int i = 0; i < question.getOptions().size(); i++) {
                if (Objects.equals(question.getOptions().get(i).getId(), currentAnswer.getId())) {
                    question.getOptions().remove(i);
                    break;
                }
            }
            questionRepository.save(question);
            answerRepository.delete(currentAnswer);
            return test;
        }
        return null;
    }

    public Test addQuestionToTest(Question question, Test test) {
        test.getQuestions().add(question);
        question.setTest(test);
        return test;
    }

    public Question addAnswerToQuestion(Answer answer, Question question) {
        question.getOptions().add(answer);
        answer.setQuestion(question);
        return question;
    }
}
