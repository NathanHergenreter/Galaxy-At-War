import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UserAccountTypes } from 'src/app/interfaces/EnumTypes';
import { LoginAuthenticatorService } from 'src/app/services/login-authenticator.service';
import { UserSessionService } from 'src/app/services/user-session.service';
import { ProfileUpdaterService } from 'src/app/services/profile-updater.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  constructor(
    private _activatedRoute: ActivatedRoute,
    private _userSession: UserSessionService,
    private _router: Router) {
    
      this._usernameTextField = this._userSession.getUsername();
      this._oldUsername = this._usernameTextField;
      this._userAccountType = this._userSession.getAccountType();
      this._bioTextField = this._userSession.getBiography();
      this._avatarUrl = this._userSession.getAvatarUrl();
      this._gamesWon = _userSession.getGamesWon();
      this._gamesPlayed = _userSession.getGamesPlayed();
  }

  //fields for avatar/picture
  _avatarToUpload: any;
  _avatarUrl: any;

  //Text fields
  _usernameTextField: string;
  _bioTextField: string;
  _userAccountType: UserAccountTypes
  _gamesWon: number;
  _gamesPlayed: number;

  //Other fields  
  _oldUsername: string;
  _accessToken: String;
  _isEditProfile: boolean = false;

  ngOnInit() {
    if (!this._userSession._isLoggedIn) {
      this._router.navigate(['signin']);
    }
  }

  onUpdate() {
    this._isEditProfile = !this._isEditProfile;
  }

  onCancel() {
    this._isEditProfile = false;
  }

  onSubmit() {
    if (!this.isFormValid()) return;
    this._isEditProfile = false;
    this._userSession.updateProfile(
      {
        username: this._usernameTextField,
        accountType: this._userAccountType,
        avatar: this._avatarUrl,
        biography: this._bioTextField,
        gamesWon: this._userSession.getGamesWon(),
        gamesPlayed: this._userSession.getGamesPlayed()
      }
    );
  }

  onImageSelected(files) {
    this._avatarToUpload = files[0];
    var reader = new FileReader();
    reader.onload = (event: any) => {
      this._avatarUrl = event.target.result;
    }
    reader.readAsDataURL(this._avatarToUpload)
  }

  isFormValid(): boolean {
    if (this._usernameTextField == null || this._usernameTextField == '') {
      this.promptUser('please enter a username');
      return false;
    }
    return true;
  }

  promptUser(message: string) {
    confirm(message);
  }

}
