package com.project.IntelligentEditorAPI.model;

public class TestRequest {

    private Test test;
    private String remark;
    private Long questionId;

    public TestRequest(Test test, String remark, Long questionId) {
        this.test = test;
        this.remark = remark;
        this.questionId = questionId;
    }

    public TestRequest() {}

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
