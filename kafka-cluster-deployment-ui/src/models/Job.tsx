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
}

export type Base = {
    id: number;
    startTimeMillis: number;
    status: Status;
    endTimeMillis: number;
}
