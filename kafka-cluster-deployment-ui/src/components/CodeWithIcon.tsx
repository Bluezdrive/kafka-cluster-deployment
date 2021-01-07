import React, {Component, ReactNode} from "react";
import {IconDefinition} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

type CodeWithIconProps = {
    display: boolean;
    icon: IconDefinition;
    text: string;
}

class CodeWithIcon extends Component<CodeWithIconProps, any> {

    public static defaultProps = {
        display: true
    };

    public render = (): ReactNode => {
        if (this.props.display) {
            return ( <div><small><FontAwesomeIcon icon={this.props.icon}/> <code>{this.props.text}</code></small></div>);
        } else {
            return null;
        }
    }

}

export default CodeWithIcon;