import React, {Component, ReactNode} from "react";
import "./LoginButton.css"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFacebook, faGithub, faGoogle} from "@fortawesome/free-brands-svg-icons";
import classNames from "classnames";
import {ClientRegistration} from "../models/ClientRegistration";
import {faSignInAlt, IconDefinition} from "@fortawesome/free-solid-svg-icons";

type LoginButtonProps = {
    clientRegistration: ClientRegistration
}

class LoginButton extends Component<LoginButtonProps, any> {

    private getIconByRegistrationId = (registrationId: string): IconDefinition => {
        switch (registrationId) {
            case 'github': return faGithub;
            case 'google': return faGoogle;
            case 'facebook': return faFacebook;
            default: return faSignInAlt;
        }
    }

    public render = (): ReactNode => {
        const clientRegistration: ClientRegistration = this.props.clientRegistration;
        const className = classNames("btn", "btn-" + clientRegistration.registrationId, "btn-block");
        return (
            <a className={className} href={"/oauth2/authorization/" + clientRegistration.registrationId}><FontAwesomeIcon icon={this.getIconByRegistrationId(clientRegistration.registrationId)}/>Sign in with {clientRegistration.clientName}</a>
        )
    }

}

export default LoginButton;