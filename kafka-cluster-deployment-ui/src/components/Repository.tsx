import React, {Component, ReactNode} from "react";
import {Job} from "../models/Job";

type RepositoryProps = {
    hideLink?: boolean,
    job: Job
}

class Repository extends Component<RepositoryProps, any> {

    public render = (): ReactNode => {
        if (this.props.hideLink) {
            return <div><b>#{this.props.job.id}</b>: <span>{this.props.job.repository}</span></div>;
        } else {
            return <div><b>#{this.props.job.id}</b>: <span><a href={this.props.job.event.repositoryUrl}>{this.props.job.repository}</a></span></div>;
        }
    }

}

export default Repository;