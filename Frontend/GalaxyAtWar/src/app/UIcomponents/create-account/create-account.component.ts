import { Component, OnInit, Injectable } from '@angular/core';
import { __spread } from 'tslib';
import { LoginAuthenticatorService } from 'src/app/services/login-authenticator.service';
import { UserAccountTypes, ServerMessages } from 'src/app/interfaces/EnumTypes';
import { Router } from '@angular/router';
import { ProfileUpdaterService } from 'src/app/services/profile-updater.service';
import { UserSessionService } from 'src/app/services/user-session.service';

@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.css'],
  providers: []
})

export class CreateAccountComponent implements OnInit {

  constructor(
    private _router: Router, 
    private _loginAuthenticator: LoginAuthenticatorService,
    private _userSession: UserSessionService) {
    this._isPasswordChecked = true;
    this._bioTextField = 'Biography:';
   }

  //fields for avatar/picture
  _avatarToUpload:any;
  _avatarUrl:string;

  //Text fields
  _usernameTextField:string;
  _passwordTextField:string;
  _passwordCheckedTextField:string;
  _bioTextField:string;

  _isPasswordChecked:boolean;

  
  ngOnInit() {
  }

  onImageSelected(files){
    this._avatarToUpload = files[0];//should only select 1 image else select first image

    //show image preview
    var reader = new FileReader();
    reader.onload = (event:any)=>{
      this._avatarUrl = event.target.result;
    }
    reader.readAsDataURL(this._avatarToUpload)
  }
  
  onUsernameInput(usernameTextField){
    this._usernameTextField = usernameTextField;
  }

  onPasswordInput(passwordTextField){
    this._passwordTextField = passwordTextField;
  }

  onPasswordValidation(passwordTextField){
    this._passwordCheckedTextField = passwordTextField;
    this._isPasswordChecked = this._passwordCheckedTextField == this._passwordTextField;
  }

  onBiographyFieldChange(text){
    this._bioTextField = text;
  }

  async onSubmit(event){
    if(!this.isFormValid()) return;

    let result = await this.createAccount(); 
    if(!result) return;

    result = await this.login();
    if(!result) return;
    
    result = await this.createProfile();
    if(!result) return;

    this.updateUserSession();
    this._router.navigate(['game-discovery-lobby']);
  }

  /**
   * Returns true if attempt was a success else false
   */
  private async createAccount(){
    let responseMsg = await this._loginAuthenticator.createAccount(
      {
        name: this._usernameTextField,
        password: this._passwordTextField
      }
    );

    switch(responseMsg){
      case (ServerMessages.success):
        return true;
      case(ServerMessages.usernameTaken):
        this.promptUser('The given username is already taken.');
        return false;
      default:
        this.promptUser('An error occurred attempting to create your account.');
        break;
    }
    return false;
  }
  private async login(){
    let responseMsg = await this._loginAuthenticator.login(
      {
        name: this._usernameTextField,
        password: this._passwordTextField
      }
    );

    switch(responseMsg){
      case (ServerMessages.success):
        return true;
      default:
        this.promptUser('An error occurred logging into your account.');
        break;
    }
    return false;
  }
  private async createProfile(){
    let responseMsg = await this._userSession.updateProfile(
      {
        username: this._usernameTextField,
        accountType: UserAccountTypes.general,
        avatar: this._avatarToUpload,
        biography: this._bioTextField,
        gamesWon: 0,
        gamesPlayed: 0
      }
    );

    switch(responseMsg){
      case (ServerMessages.success):
        return true;
      default:
        this.promptUser('Unable to create user profile.');
        break;
    }
    return false;
  }


  private isFormValid():boolean{
    if(this._usernameTextField == null || this._usernameTextField == ''){
      this.promptUser('Please enter a username');
      return false;
    }
    if(this._passwordTextField == null || this._passwordTextField == ''){
      this.promptUser('Please enter a password');
      return false;
    }
    if(!this._passwordCheckedTextField){
      this.promptUser('Password fields do not match');
      return false;
    }

    return true;
  }

  private updateUserSession(){
    this._userSession.setAvatarUrl(this._avatarUrl);
    this._userSession.setBiography(this._bioTextField);
    this._userSession.setPassword(this._passwordTextField);
    this._userSession.setUsername(this._usernameTextField);
    this._userSession._isLoggedIn = true;
  }

  /**
   * Prompts user with a message-box and parameterized message
   * @param message 
   */
  private promptUser(message:string){
    confirm(message);
  }
}
