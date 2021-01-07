import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, {Component, ReactNode} from "react";
import {Status} from "../models/Job";
import { faTimes, faCheck, faHistory, faSpinner, faBan, IconDefinition } from '@fortawesome/free-solid-svg-icons'

type StatusIconProps = {
    status: Status,
    hidden?: boolean
}

class StatusIcon extends Component<StatusIconProps, any> {

    private getIconDefinitionByStatus = (status: Status): IconDefinition => {
        if ("SUCCESS" === status.toString()) return faCheck;
        else if ("FAILED" === status.toString()) return faTimes;
        else if ("RUNNING" === status.toString()) return faSpinner;
        else if ("PENDING" === status.toString()) return faHistory;
        else return faBan;
    }

    private getSpinByStatus = (status: Status): boolean => {
        return "RUNNING" === status.toString();
    }

    public render = (): ReactNode => {
        if (this.props.hidden) {
            return null;
        }
        return (
            <span className={"icon icon-" + this.props.status.toString().toLowerCase()}>
                <FontAwesomeIcon icon={this.getIconDefinitionByStatus(this.props.status)} spin={this.getSpinByStatus(this.props.status)}/>
            </span>
        );
    }

}

export default StatusIcon;