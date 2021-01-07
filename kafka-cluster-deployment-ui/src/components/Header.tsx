import React, {Component, ReactNode} from "react";
import logo from "../logo.svg";
import './Header.css';
import LogoutButton from "./LogoutButton";

type HeaderProps = {
    authenticated: boolean
}

class Header extends Component<HeaderProps, any> {

    public render = (): ReactNode => {
        return (
            <header className="jumbotron d-flex align-items-center bg-jumbotron">
                <img src={logo} className={"logo mr-3"} alt="logo" />
                <div className={"title lh-100"}>
                    <h6 className={"mb-0 text-white lh-100"}>Apache Kafka® Cluster Deployment</h6>
                    <small className={"text-white-50"}>A deployment server for Apache Kafka® Cluster and Schema Registry</small>
                </div>
                <LogoutButton authenticated={this.props.authenticated} />
            </header>
        );
    }

}

export default Header;