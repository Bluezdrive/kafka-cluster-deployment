import React, {Component, ReactNode} from "react";

type LogoutButtonProps = {
    authenticated: boolean
}

class LogoutButton extends Component<LogoutButtonProps, any> {

    public render = (): ReactNode => {
        return (
            <form action="/logout" method="POST" className="ml-auto" hidden={!this.props.authenticated}>
                <button className="btn btn-success" type="submit">{"Sign Out"}</button>
            </form>
        );
    }

}

export default LogoutButton;