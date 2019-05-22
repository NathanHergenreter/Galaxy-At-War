import { Component, OnInit, Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { __spread } from 'tslib';
import { GameConfigService } from 'src/app/services/game-config.service';
import { ServerMessages } from 'src/app/interfaces/EnumTypes';
import { Router ,NavigationEnd} from '@angular/router';
import { UserSessionService } from 'src/app/services/user-session.service';
import { GameUpdaterService } from 'src/app/services/game-updater.service';

@Component({
  selector: 'app-game-config-lobby',
  templateUrl: './game-config-lobby.component.html',
  styleUrls: ['./game-config-lobby.component.css']
})
export class GameConfigLobbyComponent implements OnInit {

  constructor(
    private _router: Router,
    private _gameConfigService: GameConfigService,
    private _userSession: UserSessionService,
    private _gameUpdater: GameUpdaterService) {
      
    }

  //Text fields
  _nameofgameTextField: string;
  _playercountNumberField: number;
  _planetcountNumberField: number;
  _startingmoneyNumberField: number;
  _startpointNumberField: number;

  ngOnInit() {
    let flag = localStorage.getItem("isGameLoad");
    if(flag != undefined && flag != null && flag != ""){
      let flag = localStorage.setItem("isGameLoad","");
    }
    
  }

  onNameOfGameInput(nameofgameTextField) {
    this._nameofgameTextField = nameofgameTextField;
  }

  onPlayerCountInput(playercountNumberField) {
    this._playercountNumberField = playercountNumberField;
  }

  onPlanetCountInput(planetcountNumberField) {
    this._planetcountNumberField = planetcountNumberField;
  }

  onStartingMoneyInput(startingmoneyNumberField) {
    this._startingmoneyNumberField = startingmoneyNumberField;
  }

  onStartPointInput(startpointNumberField) {
    this._startpointNumberField = startpointNumberField;
  }

  async onSubmit(event) {
    if (!this.isFormValid()) return;
    let gameConfig = 
    {
      hostUsername: this._userSession.getUsername(),
      gameName: this._nameofgameTextField,
      playerCount: this._playercountNumberField,
      planetCount: this._planetcountNumberField,
      startingMoney: this._startingmoneyNumberField,
      startPoint: this._startpointNumberField
    };

    let gameId = await this._gameConfigService.setGameConfigAsync(gameConfig);
    if(gameId){
      let gameParams = {
        hostUsername: gameConfig.hostUsername,
        gameName: gameConfig.gameName,
        playerCount: gameConfig.playerCount,
        planetCount: gameConfig.planetCount,
        startingMoney: gameConfig.startingMoney,
        startPoint: gameConfig.startPoint,
        //new
        gameId: gameId
      };
      this._gameUpdater.setGameIdAsyc(gameId);
      //this._gameUpdater.sendToServer('GameCreated', 0, 0, 0, '' + this._userSession.getUsername());
      this._router.navigate(['game-pregame', gameParams]);
    }else{
        this.promptUser('An error occurred attempting to set game config lobby.');
    }
  }

  private isFormValid(): boolean {
    if (this._nameofgameTextField == null || this._nameofgameTextField == '') {
      this.promptUser('Please enter the name of the game');
      return false;
    }
    if (this._playercountNumberField == null) {
      this.promptUser('Please enter the number of players to be in the game');
      return false;
    }
    if (this._planetcountNumberField == null) {
      this.promptUser('Please enter the number of planets to be in the game');
      return false;
    }
    if (this._startingmoneyNumberField == null) {
      this.promptUser('Please enter the starting money amount');
      return false;
    }
    if (this._startpointNumberField == null) {
      this.promptUser('Please enter the amount of start position points each player gets');
      return false;
    }
    return true;
  }

  /**
   * Prompts user with a message-box and parameterized message
   * @param message 
   */
  private promptUser(message: string) {
    confirm(message);
  }
}
