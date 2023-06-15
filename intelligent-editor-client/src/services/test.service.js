import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test/';

class TestService {

    getTests() {
        return axios.get(API_URL + 'test-list', {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    generateTest(topicTest, numberQuestion, minAnswers, maxAnswers) {
        return axios.get(API_URL + 'generate-test' + '?testTopic=' + topicTest + "&numberQuestions=" +
            numberQuestion + '&minAnswers=' + minAnswers + '&maxAnswers=' + maxAnswers, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    saveTest(content, testName, id) {
        return axios
            .post(API_URL + "save-test", {
                id: id,
                title: testName,
                questions: content
            }, {headers:authHeader()})
            .then(response => {
                return response.data;
            });
    }

    getTest(id) {
        return axios.get(API_URL + 'get-test' + '?id=' + id, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    deleteTest(id) {
        return axios.get(API_URL + 'delete-test' + '?id=' + id, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    deleteQuestion(id) {
        return axios.get(API_URL + 'delete-question' + '?id=' + id, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    setQuestion(content, testName, id, remark, questionId) {
        let test = {
            id: id,
            title: testName,
            questions: content
        }

        return axios
            .post(API_URL + "set-question", {
                test: test,
                remark: remark,
                questionId: questionId
            }, {headers:authHeader()})
            .then(response => {
                return response;
            });
    }

    deleteAnswer(id) {
        return axios.get(API_URL + 'delete-answer' + '?id=' + id, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    addQuestion(testId) {
        return axios.get(API_URL + 'add-question' + '?testId=' + testId, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    getTestForExport(testId, flag) {
        return axios.get(API_URL + 'export-test' + '?testId=' + testId + '&flag=' + flag, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }

    addAnswer(questionId, correct) {
        return axios.get(API_URL + 'add-answer' + '?questionId=' + questionId + '&correct=' + correct, {headers: authHeader()})
            .then((res) => {
                return res;
            });
    }
}

export default new TestService();