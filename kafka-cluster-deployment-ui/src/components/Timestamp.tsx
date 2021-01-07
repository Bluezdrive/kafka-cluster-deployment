import React, {Component, ReactNode} from "react";
import moment from "moment";
import TextWithIcon from "./TextWithIcon";
import {faCalendarAlt} from "@fortawesome/free-solid-svg-icons";

type TimestampProps = {
    millis: number
}

type TimestampState = {
    now: number
}

class Timestamp extends Component<TimestampProps, TimestampState> {

    private intervalId?: any;

    constructor(props: TimestampProps) {
        super(props);
        this.state = {
            now: Date.now()
        }
    }

    public componentDidMount = (): void => {
        this.intervalId = setInterval(() => {
            this.setState({
                now: Date.now()
            })
        }, 1000);
    }

    public componentWillUnmount = (): void => {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    public render = (): ReactNode => {
        const duration: number = this.state.now - this.props.millis;
        const datetime: string = moment.duration(duration, "milliseconds").format("y [years], w [weeks], d [days], h [hours], m [minutes], s [seconds]", {
            largest: 1
        });
        return (<TextWithIcon text={datetime + " ago"} icon={faCalendarAlt}/>)
    }
}

export default Timestamp;