import testService from "../services/test.service";
import React, {Component} from "react";
import EventBus from "../common/EventBus";

class TestGenerator extends Component {
    content = "";
    count = 1

    constructor(props) {
        super(props);

        const windowUrl = window.location.search;
        const params = new URLSearchParams(windowUrl);
        this.state = {
            content: []
        };
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value });
        console.log(event)
    }


    generateTest(e) {
        e.preventDefault();
        this.setState({
            content: "Тест в процессе генерации...",
        });
        const hintFlagInput = document.getElementById('hintFlagInput');
        const hintFlag = hintFlagInput.checked;
        testService.generateTest(this.state.value, this.state.numberQuestion, this.state.minAnswers,
            this.state.maxAnswers, hintFlag).then(
            response => {
                console.log(response.data)
                if (response.data === "") {
                    this.setState({
                        content: "Ошибка генерации, попробуйте ещё раз",
                    });
                }
                else {
                    this.setState({
                        content: "Тест успешно сгенерирован, его можно найти в вкладке - Мои тесты"
                    });
                }
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
            });
        this.count = 1
    }

    render() {
        return (
            <div className="container">
                <header className="jumbotron">
                    <form>
                        <label >
                            <textarea className="test-topic" name="value"  type="text" placeholder="Введите тему" onChange={this.handleChange}/>
                        </label>
                        <label>
                            <input className="test-input" name="numberQuestion"  type="text" placeholder="Количество вопросов" onChange={this.handleChange}/>
                        </label>
                        <label>
                            <input className="test-input" name="minAnswers"  type="text" placeholder="Мин. число ответов" onChange={this.handleChange}/>
                        </label>
                        <label>
                            <input className="test-input" name="maxAnswers"  type="text" placeholder="Макс. число ответов" onChange={this.handleChange}/>
                        </label>
                        <label className="hintFlag">
                        <input className="hintFlag" type="checkbox" name="hintFlag" id="hintFlagInput" />
                            Подсказки к неправильным ответам
                        </label>
                        <button onClick={(e) => {
                            this.generateTest(e);
                        }}>Создать тест</button>
                    </form>
                    <h6>{this.state.content}</h6>
                </header>
            </div>
        );
    }
}

export default TestGenerator;