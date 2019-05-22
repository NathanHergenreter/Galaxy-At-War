import { Injectable } from '@angular/core';
import { LoginModel, IProfileModel } from '../interfaces/IUserModel';
import { UserAccountTypes, ServerMessages } from '../interfaces/EnumTypes';
import { HttpClient } from '@angular/common/http';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';
import { ProfileUpdaterService } from './profile-updater.service';


@Injectable({
  providedIn: 'root'
})
export class UserSessionService {

  constructor(
    //private _httpClient: HttpClient,
    //private _xmlhttp: XmlhttpService,
    private _profileUpdater: ProfileUpdaterService
    ) {
      let x = '';//bug where all user session data is lost on webpage refresh
     }

  private _username: string;
  private _password: string;
  private _accountType: UserAccountTypes;
  private _avatarUrl: any;
  private _biography: string;
  private _gamesWon: number;
  private _gamesPlayed: number;

  private _loginModel: LoginModel
  
  _isLoggedIn: boolean;

  updateUserSession(username:string, password:string){
    this._username = username;
    this._password = password;
    this._isLoggedIn = true;
    this.fetchProfileData();
  }

  async updateProfile(profile: IProfileModel) {
    this._username = profile.username;
    this._accountType = profile.accountType;
    this._avatarUrl = profile.avatar;
    this._biography = profile.biography;
    this._gamesWon = profile.gamesWon;
    this._gamesPlayed = profile.gamesPlayed;

    let response = await this._profileUpdater.updateProfileBio(profile.biography);
    if(response != ServerMessages.success)
      return response;
    response = await this._profileUpdater.updateProfileAvatar(profile.avatar);
    if(response != ServerMessages.success)
      return response;
    response = await this._profileUpdater.updateProfileGamesPlayed(profile.gamesPlayed, profile.gamesWon);
    return response;
  }

  private async fetchProfileData(){
    this._biography = await this._profileUpdater.fetchBiography(this._username);
    this._avatarUrl = await this._profileUpdater.fetchAvatarUrl(this._username);
    this._accountType = UserAccountTypes.general;
    this._gamesWon = 0;
    this._gamesPlayed = 0;
  }
  
  getUsername(){
    return this._username;
  }
  setUsername(username: string){
    this._username = username;
    this._loginModel = 
    {
      name: username,
      password: this._password
    }
  }

  getPassword(){
    return this._password;
  }
  setPassword(password: string){
    this._password = password;
    this._loginModel = 
    {
      name: this._username,
      password: password
    }
  }

  getAccountType(){
    return this._accountType;
  }
  setAccountType(type: UserAccountTypes){
    this._accountType = type;
  }

  getAvatarUrl(){
    return this._avatarUrl;
  }
  setAvatarUrl(avatar: any){
    this._avatarUrl = avatar;
    this._profileUpdater.updateProfileAvatar(avatar);
  }

  getBiography(){
    return this._biography;
  }
  setBiography(bio: string){
    this._biography = bio;
    this._profileUpdater.updateProfileBio(bio);
  }

  getGamesWon(){
    return this._gamesWon;
  }

  getGamesPlayed(){
    return this._gamesPlayed;
  }

  incrementGamesPlayed(wonGame: boolean){
    if(wonGame)this._gamesWon++;
    this._gamesPlayed++;
    this._profileUpdater.updateProfileGamesPlayed(this._gamesPlayed, this._gamesWon);
  }
}
