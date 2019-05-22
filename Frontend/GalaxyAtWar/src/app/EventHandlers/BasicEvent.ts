import { IBasicEvent } from '../interfaces/IBasicEvent';

export class BasicEvent implements IBasicEvent {
    private _handlers: { (data?): void; }[] = [];

    on(handler: (data?: any) => void): void {
        this._handlers.push(handler);
    }    
    
    off(handler: (data?: any) => void): void {
        this._handlers = this._handlers.filter(h => h!==handler);
    }
    
    trigger(data?) {
        this._handlers.slice(0).forEach(h => h(data));
    }

    expose() : IBasicEvent {
        return this;
    }
}