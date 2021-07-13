import React, {Component, ReactNode} from "react";
import {Job} from "../models/Job";
import JobItem from "./JobItem";
import "./JobDetails.css"
import TaskItem from "./TaskItem";

type JobDetailsProp = {
    job?: Job;
}

class JobDetails extends Component<JobDetailsProp, any> {

    public render = (): ReactNode => {
        if (this.props.job) {
            const job: Job = this.props.job;
            return(
                <div className="card">
                    <div className="card-header"><JobItem job={job} hideButtonRestart={true} details={true} /></div>
                    <div className="card-body card-body-nested">
                        <div className="accordion" id="tasks">
                            {job.tasks.map(task => <TaskItem key={"task-" + task.id} task={task}/>)}
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="alert alert-primary" role="alert">
                    No job selected
                </div>
            );
        }
    };

}

export default JobDetails;