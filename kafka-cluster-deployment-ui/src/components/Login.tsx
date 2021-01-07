import React, {Component, ReactNode} from "react";
import "./Login.css"
import classNames from "classnames";
import {ClientRegistration} from "../models/ClientRegistration";
import LoginButton from "./LoginButton";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSpinner} from "@fortawesome/free-solid-svg-icons";

type LoginProps = {
    clientRegistrations: ClientRegistration[];
}

type LoginState = {
    clientRegistrations: ClientRegistration[];
    loaded: boolean;
}

class Login extends Component<LoginProps, LoginState> {


    constructor(props: LoginProps) {
        super(props);

        this.state = {
            clientRegistrations: [],
            loaded: false
        }
    }

    public componentDidMount = () => {
        this.fetch().then((clientRegistrations: ClientRegistration[]) => this.setState({
            clientRegistrations: clientRegistrations,
            loaded: true
        }))
    }

    private fetch = (): Promise<ClientRegistration[]> => {
        const url: string = '/api/oauth2/registrations';
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
                } else {
                    throw response.statusText;
                }
            })
            .catch(error => console.log("Login.fetch", error));
    }

    render = (): ReactNode => {
        const searchParams: URLSearchParams = new URLSearchParams(window.location.search);
        const error: String | null = searchParams.get('error');
        const signedout: String | null = searchParams.get('signedout');
        const className = classNames({
            "is-invalid": error,
            "is-valid": !error
        });
        return (
            <div>
                <form className="form-signin text-center">
                    <h1 className="h3 mb-3 font-weight-normal">Please sign in</h1>
                    {/*<label htmlFor="inputEmail" className="sr-only">Email address</label>
                    <input type="email" id="inputEmail" className="form-control" placeholder="Email address" required={true} autoFocus={true}/>
                    <label htmlFor="inputPassword" className="sr-only">Password</label>
                    <input type="password" id="inputPassword" className="form-control" placeholder="Password" required={true}/>
                    <div className="checkbox mb-3">
                        <label>
                            <input type="checkbox" value="remember-me"/> Remember me
                        </label>
                    </div>
                    <button className="btn btn-primary btn-block" type="submit">Sign in</button>*/}
                    {this.state.loaded ? this.state.clientRegistrations.map((clientRegistration: ClientRegistration) => <LoginButton key={clientRegistration.registrationId} clientRegistration={clientRegistration}/>) : <div className={"loader"}><FontAwesomeIcon icon={faSpinner} spin={true}/></div>}
                    <div className={className}/>
                    <div hidden={!signedout} className="valid-feedback">You are now signed out</div>
                    <div hidden={!error} className="invalid-feedback">{error}</div>
                </form>
            </div>
        );
    }
}

export default Login;