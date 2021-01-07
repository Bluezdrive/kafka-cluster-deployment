import {Component, ReactNode} from "react";
import TextWithIcon from "./TextWithIcon";
import {Job} from "../models/Job";
import {faComment} from "@fortawesome/free-solid-svg-icons";

type CommitMessageProps = {
    hidden?: boolean,
    job: Job
}

class CommitMessage extends Component<CommitMessageProps, any> {

    public render = (): ReactNode => {
        if (this.props.hidden) {
            return null;
        } else {
            const text: JSX.Element =(<a href={this.props.job.event.headCommitUrl}>{this.props.job.event.headCommitMessage}</a>);
            return (<TextWithIcon text={text} icon={faComment} />)
        }
    }

}

export default CommitMessage;