import React, {Component, ReactNode} from 'react';
import './App.css';
import JobList from './components/JobList';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import Footer from "./components/Footer";
import Header from "./components/Header";
import Login from "./components/Login";
import {OAuth2Token} from "./models/OAuth2Token";

type AppState = {
    token: OAuth2Token;
}

class App extends Component<any, AppState> {

    private intervalId?: NodeJS.Timeout;

    constructor(props: any) {
        super(props);

        this.state = {
            token: this.defaultPrincipal()
        }
    }

    private defaultPrincipal = (): OAuth2Token => {
        return {
            authenticated: false,
            registrationId: "",
            id: "",
            name: "",
            email: ""
        }
    }

    public componentDidMount = (): void => {
        this.loadPrincipal();
        this.intervalId = setInterval(this.loadPrincipal, 30000);
    }

    public componentWillUnmount = (): void => {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    private loadPrincipal = (): void => {
        this.fetch()
            .then((token: OAuth2Token) => {
                this.setState({
                    token: token
                })
            });
    }

    private handleUnauthorized = (): void => {
        this.setState({
            token: this.defaultPrincipal()
        })
        if (this.intervalId && window.location.pathname === "/login.html") {
            clearInterval(this.intervalId);
        }
        if (window.location.pathname !== "/login.html") {
            window.location.replace("/login.html")
        }
    }

    private fetch = (): Promise<OAuth2Token> => {
        const url: string = '/api/oauth2/token';
        return fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then((response: Response) => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 403) {
                    this.handleUnauthorized();
                    throw response.statusText;
                } else {
                    throw response.statusText;
                }
            })
            .catch(error => console.log("App.fetch", error));
    }

    public render = (): ReactNode => {
        const authenticated: boolean = this.state.token && this.state.token.authenticated;
        return (
            <BrowserRouter>
                <main className="container-fluid mt-3">
                    <Header authenticated={authenticated}/>
                    <Switch>
                        <Route exact path="/login.html"><Login clientRegistrations={[]}/></Route>
                        <Route><JobList topic={"/topic/job"}/></Route>
                    </Switch>
                </main>
                <Footer organization="Bluezdrive" url="https://github.com/Bluezdrive" target="_blank" token={this.state.token} />
            </BrowserRouter>
        );
    }
}

export default App;
