import React, {Component, CSSProperties, MouseEvent, ReactNode} from "react";
import {Job, JobResponse} from "../models/Job";
import "./JobItem.css"
import TextWithIcon from "./TextWithIcon";
import 'moment-duration-format';
import Duration from "./Duration";
import StatusIcon from "./StatusIcon";
import User from "./User";
import Timestamp from "./Timestamp";
import Repository from "./Repository";
import CommitMessage from "./CommitMessage";
import {faCodeBranch, faRedo} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

type JobItemProps = {
    job: Job;
    details: boolean;
    hideButtonRestart: boolean;
}

class JobItem extends Component<JobItemProps, any> {

    private fetch = (jobId: number): Promise<JobResponse> => {
        const url: string = '/api/jobs/' + jobId;
        return fetch(url, {
            method: 'PUT',
            mode: 'cors',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: Response) => {
            if (response.ok) {
                return response.json();
            } else {
                throw response.statusText;
            }
        });
    }

    public handleOnClickRestartButton = (event: MouseEvent): void => {
        event.preventDefault();
        event.stopPropagation();
        this.fetch(this.props.job.id)
            .then((jobResponse: JobResponse) => {
                console.log(jobResponse.jobId)
            })
    }

    public render = (): ReactNode => {
        const job: Job = this.props.job;
        const details: boolean = this.props.details;
        const buttonStyle: CSSProperties = {
            float: "right",
            position: "absolute",
            right: ".5em",
            top: "0"
        };
        return (
            <div className="job-item">
                <div className={"row"}>
                    <div className={"col-8"}>
                        <Repository job={job} hideLink={!details}/>
                    </div>
                    <div className={"col-4"}>
                        <StatusIcon status={job.status} hidden={!details}/>
                        <span style={buttonStyle} className={"btn btn-sm btn-outline-failed"} onClick={this.handleOnClickRestartButton} hidden={details || this.props.hideButtonRestart}><FontAwesomeIcon icon={faRedo}/></span>
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
                        <TextWithIcon text={"Restarted #" + job.reference} icon={faRedo} hidden={job.reference === null}/>
                    </div>
                </div>
            </div>
        );
    };

}

export default JobItem;