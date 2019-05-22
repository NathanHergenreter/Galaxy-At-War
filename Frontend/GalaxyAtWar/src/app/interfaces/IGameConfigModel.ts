import { UserAccountTypes } from './EnumTypes';

export interface IGameConfigModel{
    hostUsername:string;
    gameName:string;
    playerCount: number;
    planetCount: number;
    startingMoney: number;
    startPoint: number;
}
