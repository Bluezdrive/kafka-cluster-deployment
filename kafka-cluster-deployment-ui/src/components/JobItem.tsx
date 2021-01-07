import React, {Component, ReactNode} from "react";
import {Job} from "../models/Job";
import "./JobItem.css"
import TextWithIcon from "./TextWithIcon";
import 'moment-duration-format';
import Duration from "./Duration";
import StatusIcon from "./StatusIcon";
import User from "./User";
import Timestamp from "./Timestamp";
import Repository from "./Repository";
import CommitMessage from "./CommitMessage";
import {faCodeBranch} from "@fortawesome/free-solid-svg-icons";

type JobItemProps = {
    job: Job;
    details: boolean;
}

class JobItem extends Component<JobItemProps, any> {

    public render = (): ReactNode => {
        const job: Job = this.props.job;
        const details: boolean = this.props.details;
        return (
            <div className="job-item">
                <div className={"row"}>
                    <div className={"col-8"}>
                        <Repository job={job} hideLink={!details}/>
                    </div>
                    <div className={"col-4"}>
                        <StatusIcon status={job.status} hidden={!details}/>
                    </div>
                </div>
                <div className={"row"}>
                    <div className={"col-xl-6"}>
                        <TextWithIcon text={"Branch " + job.branch} icon={faCodeBranch}/>
                        <Timestamp millis={job.event.repositoryPushedAt * 1000}/>
                        <CommitMessage job={job} hidden={!details || !job.event.headCommitMessage}/>
                    </div>
                    <div className={"col-xl-6"}>
                        <Duration item={job}/>
                        <User event={job.event} hidden={!details || !job.event.pusherName}/>
                    </div>
                </div>
            </div>
        );
    };

}

export default JobItem;