import { Component, OnInit } from '@angular/core';
import { LoginAuthenticatorService } from 'src/app/services/login-authenticator.service';
import { HttpClient } from '@angular/common/http';
import { ServerMessages } from 'src/app/interfaces/EnumTypes';
import { Router } from '@angular/router';
import { _sanitizeHtml } from '@angular/core/src/sanitization/html_sanitizer';
import { UserSessionService } from 'src/app/services/user-session.service';


@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
  providers: []
})
export class SigninComponent implements OnInit {

  constructor(
    private _router: Router, 
    private _loginService: LoginAuthenticatorService,
    private _userSession: UserSessionService) {
  }


  _usernameTextField: string;
  _passwordTextField: string;

  ngOnInit() { }

  onUsernameInput(usernameTextField) {
    this._usernameTextField = usernameTextField;
  }

  onPasswordInput(passwordTextField) {
    this._passwordTextField = passwordTextField;
  }

  async onSubmit() {
    let responseMsg = await this._loginService.login(
      {
        name: this._usernameTextField, 
        password: this._passwordTextField
      });
    
    switch (responseMsg) {
      case (ServerMessages.success):
        this._userSession.updateUserSession(this._usernameTextField,this._passwordTextField);
        this._router.navigate(['game-discovery-lobby']);
        break;
    }
  }
}
