import { Injectable } from '@angular/core';
import { XmlhttpService } from './xmlhttp.service';
import { UserSessionService } from './user-session.service';
import { HttpClient } from '@angular/common/http';
import { ServerMessages } from '../interfaces/EnumTypes';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(private _xmlhttp: XmlhttpService,) {

  }
  
 private MESSAGE_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/messages/";
 //private MESSAGE_URL: string = "http://localhost:8080/messages/";

  async getMessages() {
    var xhr = await this._xmlhttp.createCORSRequest('GET', this.MESSAGE_URL, null);
    console.log(xhr);
    if (!xhr) {
      throw new Error('CORS not supported');
    }
    return xhr.status != 200 ? [] : xhr.response;
  }

  async saveMessage(message) {
    var xhr = await this._xmlhttp.createCORSRequest('POST', this.MESSAGE_URL, message);
    console.log(xhr);
    if (!xhr) {
      throw new Error('CORS not supported');
    }
    return ServerMessages.success;
  }

}
