import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { UserSessionService } from './user-session.service';
import { XmlhttpService } from './xmlhttp.service';
import { TeamNotifcationsEventHandler } from '../EventHandlers/TeamNoficationsEventHandler';
import { resolve } from 'q';

@Injectable({
  providedIn: 'root'
})
export class GameUpdaterService {

  constructor(
    public _userSession: UserSessionService,
    public _xmlHttp: XmlhttpService)
  {
    this._teamUpdater = new TeamNotifcationsEventHandler();
  }

  _gameId: number;
  _teamUpdater: TeamNotifcationsEventHandler;
  private _gameClient;
  readonly SOCKET_URL = 'http://cs309-vc-1.misc.iastate.edu:8080/gamesocket';
  readonly JOIN_URL = "http://cs309-vc-1.misc.iastate.edu:8080/game/join";

  // readonly SOCKET_URL = 'http://localhost:8080/gamesocket';
  // readonly JOIN_URL = "http://localhost:8080/game/join";


  async setGameIdAsyc(id:number){
    this._gameId = id;
    return new Promise(resolve => {
      this.initializeWebSocketConnection();
      resolve();
    });
  }

  sendToServer(op, arg0, arg1, arg2, meta){
    if(this._gameClient == null) return;
    let message = {
      signal : op,
      arg0 : arg0,
      arg1 : arg1,
      arg2 : arg2,
      info : meta,
      gameId : this._gameId
    }
    this._gameClient.send("/app/signal", {}, JSON.stringify(message));
  }

  private initializeWebSocketConnection() {
    var self = this;
    var socket = new SockJS(this.SOCKET_URL);
    this._gameClient = Stomp.over(socket);
    this._gameClient.connect({}, function (frame) {
        var url = self._gameClient.ws._transport.url;
        url = url.replace("ws://cs309-vc-1.misc.iastate.edu:8080/gamesocket/", "");
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        let sessionId = url;

        try{
          console.log('subscribing at ' + self.SOCKET_URL + '/topic/response/'+sessionId);
          self._gameClient.subscribe('/topic/response/'+sessionId, function (message) {
              if(message.body) {
                //TODO: (w/msg body) look for game session object
                console.log('message returned!!');
                //TODO: trigger resp. event handlers
              }
          });
          //Tell game to join user after subscription completes
          var xhr = self._xmlHttp.createCORSRequest('POST', self.JOIN_URL
          +"?username="+self._userSession.getUsername() +"&gameid="+self._gameId+"&socketid="+sessionId, null);
        }
        catch(e){
          console.log(e);
        }
        self._gameClient.subscribe('/errors', function(message){
          console.log("ERR: " + message.body);
        });

        
        self.sendToServer('test2',0,0,0,'data');
    });
  }
}
