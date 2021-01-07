import React, {Component, ReactNode} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {IconDefinition} from "@fortawesome/free-solid-svg-icons";
import classNames from "classnames";

type AlertProps = {
    type: string,
    text: string,
    heading?: string,
    icon?: IconDefinition
}

class Alert extends Component<AlertProps, any> {

    public render = (): ReactNode => {
        const className = classNames("alert", "alert-" + this.props.type, "col-lg-4", "col-lg-offset-8", "m-auto");
        return (
            <div className={className} role="alert">
                {this.props.heading ? <h4 className="alert-heading">{this.props.icon ? <FontAwesomeIcon icon={this.props.icon} /> : undefined} {this.props.heading}</h4> : undefined}
                {this.props.heading ? <hr/> : undefined}
                <p className="mb-0">{this.props.text}</p>
            </div>
        )
    }

}

export default Alert;