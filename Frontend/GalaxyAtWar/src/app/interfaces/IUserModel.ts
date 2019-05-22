import { UserAccountTypes } from './EnumTypes';

export interface IProfileModel{
    username: string;
    accountType: UserAccountTypes;
    avatar: any;
    biography: string;
    gamesWon: number;
    gamesPlayed: number;
}

export interface LoginModel {
    name: string;
    password: string;
}

