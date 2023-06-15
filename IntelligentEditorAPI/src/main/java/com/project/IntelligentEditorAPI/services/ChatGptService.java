package com.project.IntelligentEditorAPI.services;

import cn.gjsm.api.openai.OpenAiClient;
import cn.gjsm.api.openai.OpenAiClientFactory;
import cn.gjsm.api.pojo.chat.ChatCompletionRequest;
import cn.gjsm.api.pojo.chat.ChatCompletionResponse;
import cn.gjsm.api.pojo.chat.ChatMessage;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.IntelligentEditorAPI.DTO.AnswerDTO;
import com.project.IntelligentEditorAPI.DTO.QuestionDTO;
import com.project.IntelligentEditorAPI.DTO.TestDTO;
import com.project.IntelligentEditorAPI.model.Answer;
import com.project.IntelligentEditorAPI.model.Question;
import com.project.IntelligentEditorAPI.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGptService {

    @Autowired
    TestService testService;

    public ChatGptService(TestService testService) {
        this.testService = testService;
    }

    //Specify the OpenAI API token
    String token = "token";

    OpenAiClient openAiClient =
            OpenAiClientFactory.createClient(token);

    public Test createTest(String testTopic, Integer numberQuestions) throws IOException {
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent("Сделай тест на тему - " + testTopic + " из " + numberQuestions + " вопросов в формате json, варианты ответов не нумеруй, выведи в таком формате {\n" +
                "\"questions\": [\n" +
                "        {\n" +
                "            \"question\": \"вопрос\",\n" +
                "            \"options\": [\n" +
                "                \"здесь варианты ответов\",\n" +
                "            ],\n" +
                "            \"answer\": \"здесь верный ответ\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");

        chatMessages.add(chatMessage);
        Response<ChatCompletionResponse> response = executeRequest(chatMessages);

        TestDTO testDTO = null;
        if (response.isSuccessful() && response.body() != null) {
            {
                ObjectMapper objectMapper = new ObjectMapper();
                String str = response.body().getChoices().get(0).getMessage().getContent();
                ChatMessage message = response.body().getChoices().get(0).getMessage();
                chatMessages.add(message);
                boolean isReady = true;
                while (isReady) {
                    try {
                        testDTO = objectMapper.readValue(str, TestDTO.class);
                        isReady = false;
                    } catch (JsonMappingException e) {
                        chatMessages.add(generateWithContext(chatMessages));
                        str+= chatMessages.get(chatMessages.size() - 1).getContent();
                    }
                }
            }
        }
        Test test = null;
        if (testDTO != null) {
            testDTO.setTitle(testTopic);
            test = testService.convertTestDtoToTest(testDTO);
        }

        return test;
    }

    public Test createTestWithHints(String testTopic, Integer numberQuestions, Integer minAnswers, Integer maxAnswers) throws IOException {
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent("Пожалуйста, создай для меня тест на тему " + testTopic + " в формате JSON. " +
                "Тест должен содержать " + numberQuestions +
                " вопроса. В каждом вопросе должно быть от " + minAnswers + " до " + maxAnswers +
                " вариантов ответа. У каждого вопроса должны быть варианты ответов, и количество верных ответов" +
                " должно быть случайным и разным для каждого вопроса. При неправильном ответе на вопрос," +
                " я бы хотел получить подсказку. В ответ выводи только JSON. " +
                "Пожалуйста, предоставь тест в следующем формате: {\n" +
                "\"questions\": [\n" +
                "{\n" +
                "\"question\": \"вопрос\",\n" +
                "\"options\": [(здесь варианты ответов)\n" +
                "\"option\":\n" +
                "{\n" +
                "\"text\": \"здесь текст варианта ответа\"\n" +
                "\"correct\": true если ответ правильный или false если ответ неправильный\n" +
                "\"hint\": \"здесь подсказка если ответ неправильный\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}");

        chatMessages.add(chatMessage);
        Response<ChatCompletionResponse> response = executeRequest(chatMessages);

        TestDTO testDTO = null;
        if (response.isSuccessful() && response.body() != null) {
            {
                ObjectMapper objectMapper = new ObjectMapper();
                String str = response.body().getChoices().get(0).getMessage().getContent();
                ChatMessage message = response.body().getChoices().get(0).getMessage();
                chatMessages.add(message);
                boolean isReady = true;
                while (isReady) {
                    try {
                        testDTO = objectMapper.readValue(str, TestDTO.class);
                        isReady = false;
                    } catch (JsonMappingException e) {
                        ChatMessage currentMessage = generateWithContext(chatMessages);
                        chatMessages.add(currentMessage);
                        str+= chatMessages.get(chatMessages.size() - 1).getContent();
                    }
                }
            }
        }
        Test test = null;
        if (testDTO != null) {
            testDTO.setTitle(testTopic);
            test = testService.convertTestDtoToTest(testDTO);
        }
        return test;
    }

    public ChatMessage generateWithContext(List<ChatMessage> chatMessages) throws IOException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent("Продолжай");
        chatMessages.add(chatMessage);
        Response<ChatCompletionResponse> response = executeRequest(chatMessages);
        if (response.isSuccessful() && response.body() != null) {
            return response.body().getChoices().get(0).getMessage();
        }
        return null;
    }

    public Test setQuestionInTest(Test test, Question question, String remark) throws IOException {
        QuestionDTO questionDTO = testService.questionToQuestionDto(question);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String questionJson = ow.writeValueAsString(questionDTO);
        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent("У меня есть тест на тему" + test.getTitle() + "в нём есть вопрос " + questionJson +
                "мне он не нравится,потому что" + remark + ", замени его." +
                "В ответ выведи только вопрос в формате JSON" +
                "Пожалуйста, предоставь тест в следующем формате:" +
                "{\n" +
                "\"question\": \"вопрос\",\n" +
                "\"options\": [(здесь варианты ответов)\n" +
                "\"option\":\n" +
                "{\n" +
                "\"text\": \"здесь текст варианта ответа\"\n" +
                "\"correct\": true если ответ правильный или false если ответ неправильный\n" +
                "\"hint\": \"здесь подсказка если ответ неправильный\"\n" +
                "}\n" +
                "]\n" +
                "}\n");

        chatMessages.add(chatMessage);

        Response<ChatCompletionResponse> response = executeRequest(chatMessages);

        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String str = response.body().getChoices().get(0).getMessage().getContent();
            try {
                questionDTO = objectMapper.readValue(str, QuestionDTO.class);
            } catch (JsonMappingException e) {
                return null;
            }
        }
        updateQuestionInTest(question, questionDTO);
        return test;
    }

    public Test addQuestion(Test test) throws IOException {
        TestDTO testDTO = testService.testToTestDto(test);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String testJson = ow.writeValueAsString(testDTO);
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent("У меня есть тест на тему: " + test.getTitle() + ". Вот сам этот тест - " +
                testJson + "добавь к этому тесту ещё один вопрос. " +
                "В ответ выведи только вопрос в формате JSON" +
                "Пожалуйста, предоставь тест в следующем формате:" +
                "{\n" +
                "\"question\": \"вопрос\",\n" +
                "\"options\": [(здесь варианты ответов)\n" +
                "\"option\":\n" +
                "{\n" +
                "\"text\": \"здесь текст варианта ответа\"\n" +
                "\"correct\": true если ответ правильный или false если ответ неправильный\n" +
                "\"hint\": \"здесь подсказка если ответ неправильный\"\n" +
                "}\n" +
                "]\n" +
                "}\n");

        chatMessages.add(chatMessage);
        Response<ChatCompletionResponse> response = executeRequest(chatMessages);
        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String str = response.body().getChoices().get(0).getMessage().getContent();
            QuestionDTO questionDTO;
            try {
                questionDTO = objectMapper.readValue(str, QuestionDTO.class);
            } catch (JsonMappingException e) {
                return null;
            }
            Question question = testService.convertQuestionDtoToQuestion(questionDTO);
            test = testService.addQuestionToTest(question, test);
        }
        return test;
    }

    public Question addAnswer(Question question, Boolean correct) throws IOException {
        QuestionDTO questionDTO = testService.questionToQuestionDto(question);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String questionJson = ow.writeValueAsString(questionDTO);
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        if (correct) {
            chatMessage.setContent("У меня есть есть вопрос с несколькими вариантами ответ: " + questionJson
                    + "добавь к этому вопросу ещё один правильный ответ. " +
                    "В ответ выведи только новый ответ в формате JSON" +
                    "Пожалуйста, предоставь тест в следующем формате:" +
                    "{\n" +
                    "\"text\": \"здесь текст варианта ответа\"\n" +
                    "\"correct\": true\n" +
                    "\"hint\": \"\"\n" +
                    "}\n");
        }
        else {
            chatMessage.setContent("У меня есть есть вопрос с несколькими вариантами ответ: " + questionJson
                    + "добавь к этому вопросу ещё один неправильный ответ. " +
                    "В ответ выведи только новый ответ в формате JSON" +
                    "Пожалуйста, предоставь тест в следующем формате:" +
                    "{\n" +
                    "\"text\": \"здесь текст варианта ответа\"\n" +
                    "\"correct\": false\n" +
                    "\"hint\": \"здесь напиши подсказку\"\n" +
                    "}\n");
        }
        chatMessages.add(chatMessage);
        Response<ChatCompletionResponse> response = executeRequest(chatMessages);
        if (response.isSuccessful() && response.body() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String str = response.body().getChoices().get(0).getMessage().getContent();
            AnswerDTO answerDTO;
            try {
                answerDTO = objectMapper.readValue(str, AnswerDTO.class);
            } catch (JsonMappingException e) {
                return null;
            }
            Answer answer = testService.convertAnswerDtoToAnswer(answerDTO);
            testService.addAnswerToQuestion(answer, question);
        }
        return question;
    }

    public Response<ChatCompletionResponse> executeRequest(List<ChatMessage> chatMessages) throws IOException {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(chatMessages)
                .model("gpt-3.5-turbo")
                .build();

        Call<ChatCompletionResponse> chatCompletion = openAiClient.callChatCompletion(request);
        return chatCompletion.execute();
    }

    private void updateQuestionInTest(Question question, QuestionDTO questionDTO) {
        question.setQuestion(questionDTO.getQuestion());
        List<Answer> options = question.getOptions();
        List<AnswerDTO> optionsDTO = questionDTO.getOptions();
        int size = Math.min(optionsDTO.size(), options.size());
        for (int i = 0; i < size; i++) {
            Answer option = options.get(i);
            option.setText(optionsDTO.get(i).getText());
            option.setHint(optionsDTO.get(i).getHint());
            option.setCorrect(optionsDTO.get(i).getCorrect());
        }
    }
}