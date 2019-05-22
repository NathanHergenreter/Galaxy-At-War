import { Injectable } from '@angular/core';
import { ILoginAuthenticator } from '../interfaces/ILoginAuthenticator';
import { ServerMessages } from '../interfaces/EnumTypes';
import { LoginModel, IProfileModel } from '../interfaces/IUserModel';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';

@Injectable({
  providedIn: 'root'
})
export class LoginAuthenticatorService implements ILoginAuthenticator {



  /**
   * establishes connection with server
   */
  constructor(private _xmlhttp: XmlhttpService){  }

  //server host paths
  //-----------------
   private LOGIN_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/user/login";
   private REGISTER_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/user/register";

  //local host paths
  //----------------
  // private LOGIN_URL: string = "http://localhost:8080/user/login";
   //private REGISTER_URL: string = "http://localhost:8080/user/register";



  /**
   * @param username 
   * @param password
   */
  async login(user: LoginModel): Promise<ServerMessages> {        
    try {
      //const response = await this._httpClient.get<String>(this.LOGIN_URL, user).toPromise();
      var xhr = await this._xmlhttp.createCORSRequest('POST', this.LOGIN_URL, user);
      if (!xhr) {
        throw new Error('CORS not supported');
      }
      return xhr.status != 200 ? xhr.status : ServerMessages.success;
    }
    catch (error) {
      return ServerMessages.error;
    }
  }

  /**
   * Sends IUserModel data as JSON by HTTP request
   * 
   * returns respective ServerMessage enum type
   * @param user 
   */
  async createAccount(user: LoginModel) : Promise<ServerMessages> {
    try {
        //const response = await this._httpClient.get<String>(this.LOGIN_URL, user).toPromise();
          var xhr = await this._xmlhttp.createCORSRequest('POST', this.REGISTER_URL, user);
          if (!xhr) {
              throw new Error('CORS not supported');
          }
        return ServerMessages.success;
    }
    catch (error) {
        return ServerMessages.error;
    }
  }
}
