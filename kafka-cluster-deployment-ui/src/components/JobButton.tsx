import React, {Component, ReactNode} from "react";
import {Job} from "../models/Job";
import JobItem from "./JobItem";
import "./JobButton.css"

type JobButtonProps = {
    job: Job;
    active: boolean;
    handleOnClick: (jobId: number) => void;
}

class JobButton extends Component<JobButtonProps> {

    public render = (): ReactNode => {
        const job: Job = this.props.job;
        return (
            <button className={`${this.props.active ? "active-" + job.status.toString().toLowerCase() : job.status.toString().toLowerCase()} list-group-item list-group-item-action`} onClick={() => this.props.handleOnClick(job.id)}>
                <JobItem key={job.id} job={job} details={false} />
            </button>
        );
    }

}

export default JobButton;