import testService from "../services/test.service";
import React, {Component} from "react";
import {Link} from "react-router-dom";
import EventBus from "../common/EventBus";

class TestList extends Component {
    content = [];
    count = 1;

    constructor(props) {
        super(props);

        this.state = {
            content: []
        };
    }

    componentDidMount() {
        testService.getTests().then(
            response => {
                this.setState({
                    content: response.data
                })
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
        )
    }

    render() {
        return (
            <div className="container">
                <header className="jumbotron">
                    <h1>Мои тесты:</h1>
                    {this.state.content.map(el =>
                        <div className="test-list">
                            <Link class="link-test" to={"/test-editor?id=" + el.id}>{this.count++}. {el.title}</Link>
                        </div> )}
                </header>
            </div>
        );
    }
}

export default TestList;