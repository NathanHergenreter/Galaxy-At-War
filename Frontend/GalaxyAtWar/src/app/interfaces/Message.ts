import { User } from './User';

export class Message {
    sender: User;
    receiver: User;
    date: Date;
    message: string;

    constructor(sender?: User, receiver?: User, date?: Date, message?: string) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.message = message;
    }
}