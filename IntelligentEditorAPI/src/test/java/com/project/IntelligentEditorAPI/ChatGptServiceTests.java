package com.project.IntelligentEditorAPI;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.IntelligentEditorAPI.DTO.TestDTO;
import com.project.IntelligentEditorAPI.model.Answer;
import com.project.IntelligentEditorAPI.model.Question;
import com.project.IntelligentEditorAPI.model.Test;
import com.project.IntelligentEditorAPI.services.ChatGptService;
import com.project.IntelligentEditorAPI.services.TestService;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;

public class ChatGptServiceTests {

    Test test = new Test();

    TestService testService = new TestService();

    ChatGptService chatGptService = new ChatGptService(testService);
    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void initTest() throws JsonProcessingException {
        String questionsJson = "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"question\": \"Какой тип данных в Java хранит целочисленные значения?\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"text\": \"double\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"double используется для хранения чисел с плавающей точкой\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"int\",\n" +
                "          \"correct\": true,\n" +
                "          \"hint\": null\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"String\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"String используется для работы со строками символов\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"boolean\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"boolean используется для хранения логических значений (true/false)\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"question\": \"Какой модификатор доступа позволяет обращаться к методу из любого места программы?\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"text\": \"public\",\n" +
                "          \"correct\": true,\n" +
                "          \"hint\": null\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"private\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"private позволяет обращаться к методу только внутри класса, где он объявлен\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"protected\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"protected позволяет обращаться к методу только из классов-наследников\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"question\": \"Какая из перечисленных структур данных в Java хранит элементы в порядке добавления?\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"text\": \"HashSet\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"HashSet хранит элементы в хаотическом порядке\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"LinkedList\",\n" +
                "          \"correct\": true,\n" +
                "          \"hint\": null\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"TreeSet\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"TreeSet хранит элементы в отсортированном порядке\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"HashMap\",\n" +
                "          \"correct\": false,\n" +
                "          \"hint\": \"HashMap хранит элементы в произвольном порядке, но с быстрым доступом к элементам по ключу\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        TestDTO testDTO = objectMapper.readValue(questionsJson, TestDTO.class);
        testDTO.setTitle("Java");
        test = testService.convertTestDtoToTest(testDTO);
    }

    @org.junit.Test
    public void createTest() throws IOException {
        int numberQuestions = 3;
        int minAnswers = 3;
        int maxAnswers = 4;
        String testTopic = "Java";
        Test currentTest = chatGptService
                .createTestWithHints(testTopic, numberQuestions, minAnswers, maxAnswers);
        checkTest(currentTest, testTopic, numberQuestions, minAnswers, maxAnswers);
    }

    @org.junit.Test
    public void addQuestion() throws IOException {
        Test cloneTest = copyTest(test);
        int testSize = cloneTest.getQuestions().size();
        chatGptService.addQuestion(cloneTest);
        assertSame(testSize + 1, cloneTest.getQuestions().size());
        checkTest(cloneTest, "Java", 4,3, 4);
    }

    @org.junit.Test
    public void setQuestion() throws IOException {
        Test cloneTest = copyTest(test);
        int testSize = cloneTest.getQuestions().size();
        String remark = "Простой";
        chatGptService.setQuestionInTest(cloneTest, cloneTest.getQuestions().get(0), remark);
        assertSame(testSize, cloneTest.getQuestions().size());
        checkTest(cloneTest, "Java", 3,2, 8);
    }

    @org.junit.Test
    public void addAnswer() throws IOException {
        Test cloneTest = copyTest(test);
        int questionFirstSize = cloneTest.getQuestions().get(0).getOptions().size();
        Question question = cloneTest.getQuestions().get(0);
        chatGptService.addAnswer(question, true);
        checkTest(cloneTest, "Java", 3,3, 5);
        questionFirstSize++;
        assertSame(questionFirstSize, question.getOptions().size());
        assertSame(true, question.getOptions().get(question.getOptions().size() - 1).getCorrect());
        chatGptService.addAnswer(question, false);
        checkTest(cloneTest, "Java", 3,3, 6);
        questionFirstSize++;
        assertSame(questionFirstSize, question.getOptions().size());
        assertSame(false, question.getOptions().get(question.getOptions().size() - 1).getCorrect());
    }
    private void checkTest(Test currentTest, String testTopic, int numberQuestions, int minAnswers, int maxAnswers) {
        assertNotNull(currentTest);
        assertSame(testTopic, test.getTitle());
        assertSame(numberQuestions, currentTest.getQuestions().size());
        for (Question question: currentTest.getQuestions()) {
            assertNotNull(question.getQuestion());
            assertTrue(question.getOptions().size() >= minAnswers);
            assertTrue(question.getOptions().size() <= maxAnswers);
            for (Answer answer: question.getOptions()) {
                assertNotNull(answer.getCorrect());
                assertNotNull(answer.getText());
            }
        }
    }

    private Test copyTest(Test test) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(test), Test.class);
    }
}
