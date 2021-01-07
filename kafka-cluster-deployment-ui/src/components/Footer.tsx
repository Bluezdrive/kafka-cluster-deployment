import React, {Component, ReactNode} from "react";
import './Footer.css';
import {OAuth2Token} from "../models/OAuth2Token";

type FooterProps = {
    organization: string;
    url: string;
    target: string;
    token?: OAuth2Token;
}

class Footer extends Component<FooterProps, any> {

    private userName = (): string | null => {
        if (!this.props.token || !this.props.token.authenticated) return null;

        return this.props.token.name;
    }

    public render = (): ReactNode => {
        const year: number = new Date().getFullYear();
        const name: string | null = this.userName();
        return (
            <footer className="footer">
                <div className="container-fluid">
                    <div className={"row"}>
                        <div className={"col-6"}>
                            <small hidden={!name}>Welcome {name}</small>
                        </div>
                        <div className={"col-6 text-right"}>
                            <small className="text-muted">&copy; {year} <a href={this.props.url} target={this.props.target}>{this.props.organization}</a></small>
                        </div>
                    </div>
                </div>
            </footer>
        );
    }

}

export default Footer;