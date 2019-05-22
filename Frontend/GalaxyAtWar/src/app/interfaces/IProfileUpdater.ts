import { IProfileModel } from './IUserModel';
import { ServerMessages } from './EnumTypes';

export interface IProfileUpdater {
    updateProfileBio(biography: string): Promise<ServerMessages>;
    updateProfileAvatar(avatar: any): Promise<ServerMessages>;
    updateProfileGamesPlayed(gamesPlayed:number, gamesWon:number): Promise<ServerMessages>;
    fetchBiography(username: string): Promise<string>;
    fetchAvatarUrl(username: string):Promise<string>;
}