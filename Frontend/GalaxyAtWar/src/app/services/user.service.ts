import { Injectable } from '@angular/core';
import { XmlhttpService } from './xmlhttp.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {


  constructor(private _xmlhttp: XmlhttpService) {
  }

  private API_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/user/users";
  //private API_URL: string = "http://localhost:8080/user/users";

  async getUsers() {
    var xhr = await this._xmlhttp.createCORSRequest('GET', this.API_URL, null);
    console.log(xhr);
    if (!xhr) {
      throw new Error('CORS not supported');
    }
    return xhr.status != 200 ? [] : xhr.response;
  }

}
