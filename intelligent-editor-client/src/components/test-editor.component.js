import testService from "../services/test.service";
import React, {Component} from "react";
import {Link} from "react-router-dom";
import EventBus from "../common/EventBus";

class TestEditor extends Component {
    content = [];
    count = 1
    countAnswer = 1

    constructor(props) {
        super(props);

        const windowUrl = window.location.search;
        this.state = {
            content: [],
            alert: "",
            params: new URLSearchParams(windowUrl)
        };
        this.getTest()
    }

    getTest() {
        testService.getTest(this.state.params.get('id')).then(
            response => {
                console.log(response.data, "getTest")
                this.setState({
                    content: response.data.questions,
                    testName: response.data.title,
                    testId: response.data.id
                });
            },
            error => {
                this.setState({
                    content:
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString()
                });
                if (error.response && error.response.status === 401) {
                    EventBus.dispatch("logout");
                }
            }

            );


    }

    setContent = (event) => {
        event.preventDefault()
        let flag = event.target.name.toString().at(0)
        let id = event.target.name.toString().substring(1)
        for (let i = 0; i < this.state.content.length; i++) {
            console.log(flag, id)
            if (flag === 'q' && id === this.state.content[i].id.toString()) {
                this.state.content[i].question = event.target.value;
            }
            for (let j = 0; j < this.state.content[i].options.length; j++) {
                if (id === this.state.content[i].options[j].id.toString()) {
                    if (flag === "o") {
                        this.state.content[i].options[j].text = event.target.value;
                    }
                    if (flag === "h") {
                        this.state.content[i].options[j].hint = event.target.value;
                    }
                }
            }
        }
        this.setState({
            content: this.state.content
        })
    }

    saveTest(e) {
        this.setState({
            alert: "Тест в процессе сохранения"
        })
        e.preventDefault()
        this.content = this.state.content
        testService.saveTest(this.content, this.state.testName, this.state.params.get('id')).then(
            response => {
                this.setState({
                    alert: "",
                    saveTestResponse: response.data
                })
            }
        )
    }

    deleteTest(e) {
        e.preventDefault()
        this.setState({
            alert: "Тест в процессе удаления"
        })
        testService.deleteTest(this.state.testId).then(
            response => {
                this.setState({
                    alert: "",
                    saveTestResponse: response.data
                })
            }
        )
    }

    deleteQuestion = (e) => {
        e.preventDefault()
        this.setState({
            alert: "Вопрос в процессе удаления"
        })
        this.content = this.state.content
        testService.deleteQuestion(e.target[0].value).then(
            response => {
                this.setState({
                    alert: "",
                    content: response.data.questions
                })
            }
        )
    }

    deleteAnswer = (e) => {
        e.preventDefault()
        this.setState({
            alert: "Ответ в процессе удаления"
        })
        testService.deleteAnswer(e.target[0].value).then(
            response => {
                this.setState({
                    alert: "",
                    content: response.data.questions
                })
            }
        )
    }

    setQuestion = (event) => {
        event.preventDefault()
        this.setState({
            alert: "Вопрос в процессе замены"
        })
        let remark = event.target[0].value
        let questionId = event.target[1].value
        testService.setQuestion(this.state.content, this.state.testName, this.state.testId, remark, questionId)
            .then(response => {
                this.setState({
                    alert: "",
                    content: response.data.questions
                })
            })
    }

    addQuestion = (event) => {
        event.preventDefault()
        this.setState({
            alert: "Новый вопрос в процессе генераций"
        })
        testService.addQuestion(this.state.testId)
            .then(response => {
                this.setState({
                    alert: "",
                    content: response.data.questions
                })
            })
    }

    addAnswer = (event) =>{
        event.preventDefault()
        this.setState({
            alert: "Новый ответ в процессе генерации"
        })
        let questionId = event.target[0].value
        let correct = event.target[1].value
        testService.addAnswer(questionId, correct)
            .then(response => {
                this.setState({
                    alert: "",
                    content: response.data.questions
                })
            })
    }

    render() {
        return (
            <div className="container">
                <header className="jumbotron">
                    <h4>{this.state.alert}</h4>
                    <button className="btn-test" onClick={(e) => {
                        this.addQuestion(e);
                    }}>Добавить вопрос</button>
                    <button className="btn-test" onClick={(e) => {
                        this.saveTest(e);
                    }}>Сохранить тест</button>
                    <button className="btn-test" onClick={(e) => {
                        this.deleteTest(e);
                    }}>Удалить тест</button>
                    <Link to={"/test-export?testId=" + this.state.testId}>
                        <button className="btn-test">
                            Экспорт
                        </button>
                    </Link>
                    <h1>Название теста: {this.state.testName}</h1>
                    <h6 hidden="true">{this.count = 1}</h6>
                    {this.state.content.map(el =>
                        <div>
                            <h6 hidden="true">{this.countAnswer = 1}</h6>
                            <h2>Вопрос:</h2>
                            <input className="test-input" name={"q" + el.id} value={el.question} type="text" onChange={this.setContent}/>
                            <h3>{this.count++}. {el.question}</h3>
                            <form className="remark" onSubmit={this.setQuestion}>
                                <textarea className="test-input" placeholder="Введите замечание для замены вопроса"/>
                                <input  type="text" value={el.id} hidden="true"/>
                                <br/>
                                <button className="btn-question" type="submit">Заменить вопрос</button>
                            </form>
                            <form className="remark" onSubmit={this.deleteQuestion}>
                                <input type="text" value={el.id} hidden="true"/>
                                <button className="btn-question" type="submit">Удалить вопрос</button>
                            </form>
                            <br/>
                            <div className="options">
                                <h4>Добавить ответ:</h4>
                                <form onSubmit={this.addAnswer}>
                                    <input type="text" value={el.id} hidden="true"/>
                                    <input type="text" value="true" hidden="true"/>
                                    <button className="btn-answer" type="submit">Верный</button>
                                </form>
                                <form onSubmit={this.addAnswer}>
                                    <input type="text" value={el.id} hidden="true"/>
                                    <input type="text" value="false" hidden="true"/>
                                    <button className="btn-answer" type="submit">Неверный</button>
                                </form>
                                <h3>Варианты ответа:</h3>
                                {el.options.map(option =>
                                    <div>

                                        <input className="test-input" name={"o" + option.id} value={option.text} type="text" onChange={this.setContent}/>
                                        <h4>{this.countAnswer++}. {option.text}</h4>
                                        <h5 hidden={!option.correct}>Ответ верный</h5>
                                        <div className="hint">
                                            <h5>Подсказка:</h5>
                                            <input className="test-input" name={"h" + option.id} value={option.hint} type="text" onChange={this.setContent}/>
                                            <h5>{option.hint}</h5>
                                        </div>
                                        <form onSubmit={this.deleteAnswer}>
                                            <input type="text" value={option.id} hidden="true"/>
                                            <button className="btn-answer">Удалить ответ</button>
                                        </form>
                                        <br/><br/>
                                    </div>)}
                            </div>
                            <br/>
                        </div> )}
                </header>
            </div>
        );
    }
}

export default TestEditor;