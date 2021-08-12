import React, {Component, ReactNode} from "react";
import {Task} from "../models/Job";
import CodeWithIcon from "./CodeWithIcon";
import Duration from "./Duration";
import StatusIcon from "./StatusIcon";
import Log from "./Log";
import {faSignOutAlt, faTasks, faVirus, IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {faGitSquare} from "@fortawesome/free-brands-svg-icons";

type TaskItemProps = {
    task: Task
}

class TaskItem extends Component<TaskItemProps, any> {

    public getCommandIcon = (taskType: string): IconDefinition => {
        switch (taskType) {
            case "GIT": return faGitSquare;
            case "COMMAND_LINE": return faTasks;
            default: return faVirus;
        }
    }

    public render = (): ReactNode => {
        const task: Task = this.props.task;
        const key: string = task.key;
        return(
            <div key={key} className="card">
                <div className="card-header" id={"heading-" + key}>
                    <button className="btn text-left collapsed ml-0" type="button" data-toggle="collapse" data-target={"#collapse-" + key} aria-expanded="true" aria-controls={"collapse-" + key}/>
                    <span className={"card-header-name"}>{task.name}</span>
                    <StatusIcon status={task.status}/>
                </div>
                <div id={"collapse-" + key} className="collapse" aria-labelledby={"heading-" + key} data-parent="#tasks">
                    <div className="card-body">
                        <CodeWithIcon icon={this.getCommandIcon(task.type)} text={task.command} />
                        <CodeWithIcon display={task.exitCode > -1} icon={faSignOutAlt} text={"Process finished with exit code " + task.exitCode} />
                        <Duration item={task}/>
                    </div>
                    <Log log={task.log}/>
                </div>
            </div>
        );
    }

}

export default TaskItem;