import React, {Component, ReactNode} from "react";
import "./User.css"
import {Event} from "../models/Job";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUser} from "@fortawesome/free-solid-svg-icons";

type UserProps = {
    event: Event,
    hidden?: boolean
}

class User extends Component<UserProps, any> {

    public render = (): ReactNode => {
        if (this.props.hidden) {
            return null;
        } else {
            return (
                <div className={"user"}>
                    <small>
                        {
                            this.props.event.senderAvatarUrl
                                ? <div><img alt={"avatar"} className={"avatar"} src={this.props.event.senderAvatarUrl}/></div>
                                : <span className={"symbol"}><FontAwesomeIcon icon={faUser}/></span>
                        }
                        {this.props.event.pusherName + "<" + this.props.event.pusherEmail + ">"}
                    </small>
                </div>
            );
        }
    }
}

export default User;