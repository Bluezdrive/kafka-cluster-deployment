import React, {Component, ReactNode} from "react";
import {Base} from "../models/Job";
import moment from "moment";
import TextWithIcon from "./TextWithIcon";
import {faStopwatch} from "@fortawesome/free-solid-svg-icons";

type DurationProps = {
    item: Base
}

class Duration extends Component<DurationProps, any> {

    public render = (): ReactNode => {
        let endTimeMillis = this.props.item.endTimeMillis > 0 ? this.props.item.endTimeMillis : Date.now();
        const diff: number = endTimeMillis - this.props.item.startTimeMillis;
        let date = new Date(diff);
        date.setHours(date.getHours() - 1);
        const datetime: string = moment.duration(diff, "milliseconds").format("y [years], w [weeks], d [days], h [hours], m [minutes], s [seconds], S [milliseconds]", {
            largest: 1
        });
        let text: string = 'Not started';
        if (this.props.item.status.toString() === "SUCCESS" || this.props.item.status.toString() === "FAILED") {
            text = "Ran for " + datetime;
        } else if (this.props.item.status.toString() === "RUNNING") {
            text = "Running for " + datetime;
        }
        return (<TextWithIcon text={text} icon={faStopwatch}/>)
    }

}

export default Duration;