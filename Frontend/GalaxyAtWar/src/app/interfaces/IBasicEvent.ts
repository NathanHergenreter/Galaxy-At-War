export interface IBasicEvent {
    on(handler: { (data?): void }) : void;
    off(handler: { (data?): void }) : void;
    trigger(data);
}