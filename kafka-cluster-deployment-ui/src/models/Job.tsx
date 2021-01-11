export enum Status {
    CREATED, RUNNING, SUCCESS, FAILED
}

export type Task = Base & {
    key: string;
    name: string;
    command: string;
    exitCode: number;
    log: string;
}

export type Event = {
    id: string;
    ref: string;
    repositoryFullName: string;
    repositoryUrl: string;
    repositoryPushedAt: number;
    pusherName: string;
    pusherEmail: string;
    senderAvatarUrl: string;
    senderUrl: string;
    headCommitId: string;
    headCommitMessage: string;
    headCommitTimestamp: number;
    headCommitUrl: string;
}

export type Job = Base & {
    tasks: Task[];
    repository: string;
    branch: string;
    event: Event;
    reference: number;
}

export type GitPollingLog = Base & {
    repository: string;
    branch: string;
    headCommitId: string;
    remoteObjectId: string;
    changed: boolean;
    error: string;
    job: number;
}

export type Base = {
    id: number;
    startTimeMillis: number;
    status: Status;
    endTimeMillis: number;
    createdDate: number;
    lastModifiedDate: number;
}

export type JobResponse = Error & {
    jobId: number;
}

export type Error = {
    timestamp: number;
    status: number;
    error: string;
    message: string;
    path: string;
}
