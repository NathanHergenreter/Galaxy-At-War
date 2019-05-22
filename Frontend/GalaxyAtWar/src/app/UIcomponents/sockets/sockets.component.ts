import { Component, OnInit } from '@angular/core';
import { ChatService } from 'src/app/services/chat.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { Message } from 'src/app/interfaces/Message';
import { UserSessionService } from 'src/app/services/user-session.service';
import { UserService } from 'src/app/services/user.service';
import { GameConfigService } from 'src/app/services/game-config.service';

@Component({
  selector: 'app-sockets',
  templateUrl: './sockets.component.html',
  styleUrls: ['./sockets.component.css']
})
export class SocketsComponent implements OnInit {

  private chatUrl = 'http://cs309-vc-1.misc.iastate.edu:8080/socket';
  private serverUrl = 'http://cs309-vc-1.misc.iastate.edu:8080/broadcast';
  // private serverUrl = 'http://localhost:8080/broadcast';
  // private chatUrl = 'http://localhost:8080/socket';
  private title = "Sockets";

  stompClient = null;
  messages = [];
  message = '';

  constructor(private _userSession: UserSessionService, 
              private chatService: ChatService, 
              private userService: UserService,
              private _gameConfig: GameConfigService) {
    this.connect();
  }

  connect() {
    var self = this;
    var socket = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({ user: this._userSession.getUsername() }, function (frame) {
      var url = self.stompClient.ws._transport.url;
      url = url.replace(
        "ws://localhost:8080/socket/", "");
      url = url.replace("/websocket", "");
      url = url.replace(/^[0-9]+\//, "");
      self.stompClient.subscribe('/topic/game/' + this._gameConfig.sessionId /*self.sessionId*/, function (message) {
        if (message.body) {
          self.messages.push(JSON.parse(message.body));
        }
      });
    });
  }

  sendMessage(data: any) {
      this.stompClient.send("/app/game/send-message", {}, JSON.stringify(data));
  }

  ngOnInit() {
  }

}
