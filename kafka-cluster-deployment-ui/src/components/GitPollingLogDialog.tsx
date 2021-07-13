import React, {Component, ReactNode} from "react";
import {GitPollingLog} from "../models/Job";

type GitPollingLogDialogProps = {
    gitPollingLog?: GitPollingLog;
}

class GitPollingLogDialog extends Component<GitPollingLogDialogProps, any> {

    private text = (): ReactNode => {
        const gitPollingLog: GitPollingLog | undefined = this.props.gitPollingLog;
        if (gitPollingLog) {
            const lastModifiedDate = new Intl.DateTimeFormat("en-US", {
                month: "short",
                year: "numeric",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
            }).format(gitPollingLog.lastModifiedDate);
            return (
                <code>
                    Started on {lastModifiedDate}<br/>
                    [poll] Last Built Revision: Revision {gitPollingLog.headCommitId} ({gitPollingLog.branch})<br/>
                    &gt; git ls-remote --head git@github.com:{gitPollingLog.repository}.git<br/>
                    [poll] Latest remote head revision on {gitPollingLog.branch} is: {gitPollingLog.remoteObjectId} - already built by {gitPollingLog.job}<br/>
                    Done. Took {(gitPollingLog.endTimeMillis - gitPollingLog.startTimeMillis) / 1000} sec<br/>
                    {gitPollingLog.changed ? "" : "No changes"}
                </code>
            );
        } else {
            return (
                <pre>No git polling log available.</pre>
            )
        }
    }

    public render = (): ReactNode => {
        return (
            <div className="modal fade" id="exampleModal" tabIndex={-1} aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div className="modal-dialog modal-lg">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="exampleModalLabel">Git Polling Log</h5>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <small>
                                <pre>{this.text()}</pre>
                            </small>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

}

export default GitPollingLogDialog;