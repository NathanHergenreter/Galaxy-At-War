import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserSessionService } from 'src/app/services/user-session.service';
import { GameConfigService } from 'src/app/services/game-config.service';
import { compileNgModuleFromRender2 } from '@angular/compiler/src/render3/r3_module_compiler';
import { GameUpdaterService } from 'src/app/services/game-updater.service';
import { getAttrsForDirectiveMatching } from '@angular/compiler/src/render3/view/util';

@Component({
  selector: 'app-game-discovery-lobby',
  templateUrl: './game-discovery-lobby.component.html',
  styleUrls: ['./game-discovery-lobby.component.css']
})
export class gameDiscoveryLobbyComponent implements OnInit {
  
  constructor(
    private _router: Router,
    private _userSession: UserSessionService,
    private _gameConfig: GameConfigService,
    private _gameUpdater: GameUpdaterService) {
      this.fetchAvailableGames();

    }

  ngOnInit() {
    let isLogged = this._userSession._isLoggedIn;
    if (isLogged != true) {
      this._router.navigate(['signin']);
    }
  }
  private gotoGameConfigPage(){
    this._router.navigate(['game-config-lobby']); 
  }
  private gotoProfilePage() {
    this._router.navigate(['profile']);
  }

  private async fetchAvailableGames(){
    this._data = new Array();
    var dbData = await this._gameConfig.getActiveGameConfigsAsync();

    for(var i = 0; i < dbData.length; i++)
    {
      if(dbData[i].config.playerCount != 0 
        && dbData[i].config.playerCount < dbData[i].playerNeeded)
      {
        this._data.push(dbData[i]);
      }
    }
  }

  private onJoin(gameData){
    try{
      let gameConfig = gameData.config;
      let gameParams = {
        hostUsername: gameConfig.hostUsername,
        gameName: gameConfig.gameName,
        playerCount: gameConfig.playerCount,
        planetCount: gameConfig.planetCount,
        startingMoney: gameConfig.startingMoney,
        startPoint: gameConfig.startPoint,
        //new
        gameId: gameConfig.gameId
      }
      this.navigateToPregameLobbyAsyc(gameParams);
    }
    catch (e){
      console.log(e);
      this.promptUser('Unable to fetch selected game configurations.')
    }
  }

  private async navigateToPregameLobbyAsyc(gameParams){
    try{
      await this._gameUpdater.setGameIdAsyc(gameParams.gameId);
      this._gameUpdater.sendToServer('GameCreated', gameParams.gameId, 0, 0, '' + this._userSession.getUsername());
      this._router.navigate(['game-pregame', gameParams]);
    }
    catch (e){
      console.log(e);
      this.promptUser('Unable to establish a web socket connection.')
      this._router.navigate(['game-pregame', gameParams]);
    }
  }

  private promptUser(message:string){
    confirm(message);
  }

  private _data;
}
