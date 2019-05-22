import { Component, OnInit } from '@angular/core';
import * as SockJS from 'sockjs-client';
import { Message } from 'src/app/interfaces/Message';
import { User } from 'src/app/interfaces/User';
import { ChatService } from 'src/app/services/chat.service';
import { UserSessionService } from 'src/app/services/user-session.service';
import { UserService } from 'src/app/services/user.service';
import * as Stomp from 'stompjs';


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  _username: string;
  privateStompClient = null;
  publicStompClient = null;
  messages = [];
  users = [];
  message = '';
  sessionId = '';
  receiverUsername;

  constructor(private _userSession: UserSessionService, private chatService: ChatService, private userService: UserService) {
    this.publicConnection();
    this.privateConnection();
    this.initUsers();
    this._username = this._userSession.getUsername();
  }

  ngOnInit() {
    this.loadMessages();
  }

  publicConnection() {
    var self = this;
    //var socket = new SockJS('http://localhost:8080/socket');
    var socket = new SockJS('http://cs309-vc-1.misc.iastate.edu:8080/socket');
    this.publicStompClient = Stomp.over(socket);
    this.publicStompClient.connect({}, function (frame) {
      self.publicStompClient.subscribe('/topic/chat', function (message) {
        if (message.body) {
          self.messages.push(JSON.parse(message.body));
        }
      });
    });
  }

  privateConnection() {
    var self = this;
    var socket = new SockJS('http://localhost:8080/socket');
    this.privateStompClient = Stomp.over(socket);
    this.privateStompClient.connect({ user: this._userSession.getUsername() }, function (frame) {
      var url = self.privateStompClient.ws._transport.url;
      url = url.replace(
        "ws://localhost:8080/socket/", "");
      url = url.replace("/websocket", "");
      url = url.replace(/^[0-9]+\//, "");
      self.sessionId = url;
      self.privateStompClient.subscribe('/topic/chat/' + self.sessionId, function (message) {
        if (message.body) {
          self.messages.push(JSON.parse(message.body));
        }
      });
    });
  }

  sendMessage() {
    let mode;
    let stompClient;
    if (this.receiverUsername) {
      stompClient = this.privateStompClient;
      mode = 'private';
    } else {
      stompClient = this.publicStompClient;
      mode = 'public';
    }
    let data = new Message(new User(this._username), new User(this.receiverUsername), new Date(), this.message);
    stompClient.send("/app/chat/" + mode + "/send-message", {}, JSON.stringify(data));
    this.message = '';
  }

  loadMessages() {
    this.chatService.getMessages().then(
      resp => {
        this.messages = this.messages.concat(JSON.parse(resp));
      },
      error => {
        console.log(error);
      })
  }

  initUsers() {
    this.userService.getUsers().then(
      resp => {
        this.users = JSON.parse(resp);
      },
      error => {
        console.log(error);
      })

  }

  selectUser(user) {
    this.receiverUsername = user.username;
  }

  selectPublic() { 
    this.receiverUsername = undefined;
  }

}
