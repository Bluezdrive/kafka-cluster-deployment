import React, {Component, ReactNode} from "react";
import {Job} from "../models/Job";
import JobButton from "./JobButton";
import JobDetails from "./JobDetails";
import "./JobList.css"
import {Client, IMessage, StompSubscription} from "@stomp/stompjs";
import Page from "../models/Page";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationTriangle, faSpinner} from "@fortawesome/free-solid-svg-icons";
import Alert from "./Alert";
import SockJS from "sockjs-client";

const PAGE_SIZE: number = 10;

type JobListProps = {
    topic: string
}

type JobListState = {
    jobs: Job[];
    jobId: number;
    job?: Job;
    error?: string;
    loaded: boolean;
    skip: number;
    page: number;
    total: number;
}

class JobList extends Component<JobListProps, JobListState> {

    private client: Client;
    private subscription: StompSubscription | undefined;

    constructor(props: JobListProps) {
        super(props);

        const url: string = "http://localhost:8080/ws";
        this.state = this.initState();
        this.client = new Client({
            webSocketFactory: () => new SockJS(url),
            onConnect: this.handleOnConnect,
            onWebSocketError: (event: ErrorEvent) => {
                this.setState({
                    error: "Connection error"
                })
            },
            onWebSocketClose: (event: CloseEvent) => {
                if (event.reason && event.code !== 1000) {
                    this.setState({
                        error: "Connection closed (" + event.code + "): " + event.reason
                    })
                }
            }
        });
        this.handleOnConnect = this.handleOnConnect.bind(this);
        this.handleOnClickLoadMoreJobsButton = this.handleOnClickLoadMoreJobsButton.bind(this);
        this.handleOnLoaded = this.handleOnLoaded.bind(this);
        this.handleOnMessage = this.handleOnMessage.bind(this);
        this.handleOnClickJobButton = this.handleOnClickJobButton.bind(this);
    }

    public componentDidMount = (): void => {
        this.client.activate();
    }

    public componentWillUnmount = (): void => {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        this.client.onWebSocketClose = () => {};
        this.client.deactivate().then();
    }

    private initState = (): JobListState => {
        return {
            jobs: [],
            jobId: 0,
            error: undefined,
            loaded: false,
            skip: 0,
            page: 0,
            total: 0
        };
    }

    private fetch = (skip: number, page: number): Promise<Page<Job>> => {
        const url: string = '/api/jobs?skip=' + skip + '&page=' + page + '&size=' + PAGE_SIZE + '&sort=id,desc';
        return fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then((response: Response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw response.statusText;
                }
            });
    }

    private readonly handleOnClickJobButton = (jobId: number): void => {
        const jobs: Job[] = this.state.jobs;
        this.setState({
            jobId: jobId,
            job: jobs.find(job => job.id === jobId)
        });
    }

    private readonly handleOnClickLoadMoreJobsButton = (): void => {
        let page: number = this.state.page + 1;
        this.fetch(this.state.skip, page)
            .then((page: Page<Job>) => page.content)
            .then((newJobs: Job[]) => {
                const jobs: Job[] = this.state.jobs;
                newJobs.forEach((newJob: Job) => {
                    jobs.push(newJob);
                });
                this.setState({
                    jobs: jobs.sort((a: { id: number; }, b: { id: number; }) => b.id - a.id),
                    page: page
                });
            })
            .catch(error => {
                this.setState({ jobId: 0, job: undefined, jobs: [], error: error });
                throw error;
            })

    }

    private readonly handleOnConnect = (): void => {
        this.setState({
            jobs: [],
            job: undefined,
            error: undefined,
            loaded: false,
            skip: 0,
            page: 0,
            total: 0,
        });
        this.fetch(this.state.skip, 0)
            .then((page: Page<Job>) => {
                this.setState({
                    total: page.totalElements
                });
                return page.content;
            })
            .then((jobs: Job[]) => this.handleOnLoaded(jobs))
            .catch(error => {
                this.setState({ jobId: 0, job: undefined, jobs: [], error: error.toString() });
                console.log("Error: ", error);
                throw error;
            })
    }

    private handleOnLoaded = (jobs: Job[]): void => {
        const jobId: number = this.getSelectedJobId(jobs);
        this.setState({
            jobs: jobs,
            jobId: jobId,
            job: this.findJobById(jobs, jobId),
            loaded: true
        });
        this.subscription = this.client.subscribe(this.props.topic, this.handleOnMessage);
    }

    private getSelectedJobId = (jobs: Job[]): number => {
        if (this.state.jobId > 0) {
            return this.state.jobId;
        } else if (jobs.length > 0) {
            return jobs[0].id;
        } else {
            return 0;
        }
    }

    private findJobById = (jobs: Job[], id: number): Job | undefined => {
        if (id === 0) {
            return undefined;
        }
        const job: Job | undefined = jobs.find((job: Job) => job.id === id);
        if (job) {
            return job;
        } else {
            return jobs[0];
        }
    }

    private readonly handleOnMessage = (message: IMessage): void => {
        const jobs: Job[] = this.state.jobs;
        const job: Job = JSON.parse(message.body);
        if (job.status.toString() === 'CREATED') {
            jobs.unshift(job);
            this.setState({
                skip: this.state.skip + 1
            });
        } else {
            const jobId: number = job.id;
            jobs.forEach((item: Job, index: number) => {
                if (item.id === jobId) {
                    jobs[index] = job;
                }
            });
        }
        this.setState({
            jobs: jobs
        });
        if (this.state.jobId === job.id || this.state.jobId === 0) {
            this.setState({
                job: job,
                jobId: job.id
            });
        }
    }

    public render = (): ReactNode => {
        const jobs: Job[] = this.state.jobs;
        if (this.state.error) {
            return (<Alert heading={"Error"} icon={faExclamationTriangle} type={"danger"} text={this.state.error}/>)
        } else if (this.state.loaded && jobs.length === 0) {
            return (<Alert heading={"Info"} type={"primary"} text={"No jobs have been executed so far"}/>)
        } else if (this.state.loaded && jobs.length > 0) {
            return (
                <div className="row">
                    <div className="col-md-4">
                        <div className="list-scroll">
                            <div className="list-group">
                                {jobs.map(job => <JobButton key={job.id} active={this.state.jobId === job.id} job={job} handleOnClick={this.handleOnClickJobButton}/>)}
                                <div hidden={jobs.length >= this.state.total} className="list-group-item">
                                    <button className={"btn btn-sm btn-block btn-primary"} onClick={this.handleOnClickLoadMoreJobsButton}>Load more jobs...</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-8 pl-md-0 pt-3 pt-md-0">
                        <div className="list-scroll" >
                            <JobDetails job={this.state.job} />
                        </div>
                    </div>
                </div>
            );
        } else {
            return (<div className={"loader"}><FontAwesomeIcon icon={faSpinner} spin={true}/></div>);
        }
    }

}

export default JobList;