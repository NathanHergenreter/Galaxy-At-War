import { Injectable } from '@angular/core';
import { IProfileUpdater } from '../interfaces/IProfileUpdater';
import { ServerMessages } from '../interfaces/EnumTypes';
import { IProfileModel } from '../interfaces/IUserModel';
import { HttpClient } from '@angular/common/http';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';
import { UserSessionService } from './user-session.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileUpdaterService implements IProfileUpdater {
  
  constructor(
    private _httpClient: HttpClient,
    private _xmlhttp: XmlhttpService,
    ){}

  //server host paths
  //-----------------
    private PROFILE_BIO_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/user/profile/bio";
    private PROFILE_AVATAR_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/user/profile/avatar";

  //local host paths
  //----------------
  // private PROFILE_BIO_URL: string = "http://localhost:8080/user/profile/bio";
  // private PROFILE_AVATAR_URL: string = "http://localhost:8080/user/profile/avatar";

  async updateProfileAvatar(avatar: any): Promise<ServerMessages> {
    try {
        var xhr = await this._xmlhttp.createCORSRequest('POST', this.PROFILE_AVATAR_URL, avatar);
        if (!xhr) {
            throw new Error('CORS not supported');
        }
        return ServerMessages.success;
    }
    catch (error) {
      return ServerMessages.error;
    }
  }

  async updateProfileBio(biography: string): Promise<ServerMessages> {
    try {
      var xhr = await this._xmlhttp.createCORSRequest('POST', this.PROFILE_BIO_URL, biography);
      if (!xhr) {
          throw new Error('CORS not supported');
      }
      return ServerMessages.success;
    }
    catch (error) {
      return ServerMessages.error;
    }
  }

  async updateProfileGamesPlayed(gamesPlayed:number, gamesWon:number): Promise<ServerMessages>{
    return ServerMessages.success;
  }

  async fetchBiography(username: string): Promise<string> {
    var xhr = this._xmlhttp.createCORSRequest('GET', this.PROFILE_BIO_URL +
    "?username=" + username, null);
    return xhr.responseText;
  }

  async fetchAvatarUrl(username: string):Promise<string>{
    try {
      var xhr = await this._xmlhttp.createCORSRequest('POST', this.PROFILE_BIO_URL +
      "?username=" + username, null);
      if (!xhr) {
        throw new Error('CORS not supported');
      }
      return xhr.toString();
    }
    catch (error) {
      return "";
    }
  }
}
