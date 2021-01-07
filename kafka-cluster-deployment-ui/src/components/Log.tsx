import Ansi from "ansi-to-react";
import React, {Component, ReactNode} from "react";

type LogProps = {
    log?: string
}

class Log extends Component<LogProps, any> {

    public render = (): ReactNode => {
        if (this.props.log) {
            return (
                <div className="card-body logs">
                    <div className="log-line"><Ansi>{this.props.log}</Ansi></div>
                </div>
            );
        } else {
            return null;
        }
    }

}

export default Log;