import testService from "../services/test.service";
import React, {Component} from "react";
import EventBus from "../common/EventBus";

class TestExport extends Component {
    content = [];

    constructor(props) {
        super(props);

        const windowUrl = window.location.search;
        this.state = {
            content: "",
            typeFile: "txt",
            alert: "",
            params: new URLSearchParams(windowUrl)
        };
    }

    getTestForExport(event) {
        event.preventDefault()
        if (event.target.name === "gift")
            this.setState({
                typeFile: "gift",
                alert: "Тест в формате GIFT"
            });
        if (event.target.name === "csv")
            this.setState({
                typeFile: "csv",
                alert: "Тест в формате CSV"
            });
        if (event.target.name === "txt")
            this.setState({
                typeFile: "txt",
                alert: "Тест в текстовом формате"
            });
        testService.getTestForExport(this.state.params.get('testId'), event.target.name).then(
            response => {
                console.log(response.data, "check")
                this.setState({
                    content: response.data
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
            });
    }

    downloadTestFile(event) {
        event.preventDefault()
        let text = this.state.content;
        let blob = new Blob([text], {type: "text/plain"});
        let link = document.createElement("a");
        let fileName = "test" + this.state.params.get('testId') + "." + this.state.typeFile
        link.setAttribute("href", URL.createObjectURL(blob));
        link.setAttribute("download", fileName);
        link.click();
    }

    setContent = (event) => {
        event.preventDefault()
        this.setState({
            content: event.target.value
        });
    }

    render() {
        return (
            <div className="container">
                <header className="jumbotron">
                    <button className ="btn-test" onClick={(e) => {
                        this.downloadTestFile(e);
                    }}>Скачать тест</button>
                    <h4>Выберете формат для экспорта:</h4>
                    <button name="csv" className="btn-test" onClick={(e) => {
                        this.getTestForExport(e);
                    }}>CSV</button>
                    <button name="gift" className="btn-test" onClick={(e) => {
                        this.getTestForExport(e);
                    }}>GIFT</button>
                    <button name="txt" className="btn-test" onClick={(e) => {
                        this.getTestForExport(e);
                    }}>TXT</button>
                    <h5>{this.state.alert}</h5>
                    <textarea className="test-export" value={this.state.content} onChange={this.setContent}></textarea>
                </header>
            </div>
        );
    }
}

export default TestExport;