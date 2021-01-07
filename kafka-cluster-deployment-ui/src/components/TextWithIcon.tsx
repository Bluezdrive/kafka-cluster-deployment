import React, {Component, ReactNode} from "react";
import {IconDefinition} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

type TextWithIconProps = {
    display: boolean;
    className: string;
    icon: IconDefinition;
    text: JSX.Element | string;
}

class TextWithIcon extends Component<TextWithIconProps, any> {

    public static defaultProps = {
        display: true,
        className: ""
    };

    public render = (): ReactNode => {
        if (this.props.display) {
            return (
                <div className={this.props.className}>
                    <small>
                        <span className={"symbol"}><FontAwesomeIcon icon={this.props.icon}/></span> {this.props.text}
                    </small>
                </div>
            );
        } else {
            return null;
        }
    };

}

export default TextWithIcon;