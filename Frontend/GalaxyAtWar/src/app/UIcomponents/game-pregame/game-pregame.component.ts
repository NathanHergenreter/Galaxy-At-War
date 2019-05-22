import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UserSessionService } from 'src/app/services/user-session.service';
import { GameConfigService } from 'src/app/services/game-config.service';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';
import { TestBed } from '@angular/core/testing';
import { GameUpdaterService } from 'src/app/services/game-updater.service';
import { TeamNotifcationsEventHandler } from 'src/app/EventHandlers/TeamNoficationsEventHandler';

@Component({
    selector: 'app-game-pregame',
    templateUrl: './game-pregame.component.html',
    styleUrls: ['./game-pregame.component.css']
  })
  
export class GamePregameComponent implements OnInit {

  constructor(
    private _router: Router,
    private _route: ActivatedRoute, 
    private _gameUpdater: GameUpdaterService,
    private _userSession: UserSessionService
    ) {
      this._gameConfig = this._route.snapshot.params;
      this.curNumPlayers = 0;
      this.playersNeeded = parseInt(this._gameConfig.playerCount);
      this.planetCount = parseInt(this._gameConfig.planetCount);
      this.currentPlayer = _userSession.getUsername();
      this._gameUpdater._teamUpdater.NewTeamMemberAdded.on(this.onNewTeamMemberAdded);
      this.onNewTeamMemberAdded({name: this._userSession.getUsername()});
  }

  _gameConfig;
  selectedColor;
  currentPlayer;
  _players = [];
  curNumPlayers:number;
  playersNeeded:number;
  planetCount:number;
  colors = ['#283AD6','#D61919','#2ED33C','#FFE74C', "#9C26B3", "#F7810C", "#0CEEF7", "A9AEBA"];

  ngOnInit() {
  }

  onNewTeamMemberAdded(teamMember){
    this._players.push({
      name: teamMember.name,
      color: this.colors[this.curNumPlayers]
    });
    this.curNumPlayers++;
  }
    
  private onExit() {
    this.unsubscribeEvents();
    this._router.navigate(['game-discovery-lobby']);
  }
  
  private onReady() {
    if(this.curNumPlayers < this.playersNeeded || this.playersNeeded == 0){
      if(confirm('Not all of the players have joined yet. Continue anyway?')){
        let gameParams = {
          hostUsername: this._gameConfig.hostUsername,
          gameName: this._gameConfig.gameName,
          playerCount: this._gameConfig.playerCount,
          planetCount: this._gameConfig.planetCount,
          startingMoney: this._gameConfig.startingMoney,
          startPoint: this._gameConfig.startPoint,
          gameId: this._gameConfig.gameId,
          //new
          playerColor: this.selectedColor
        };
        this.unsubscribeEvents();
        this._router.navigate(['game-dashboard',this._gameConfig]); 
      }
    }
  }
    
  selectColor( newColor) {
    this.selectedColor = newColor;
  }

  private unsubscribeEvents(){
    this._gameUpdater._teamUpdater.NewTeamMemberAdded.off(this.onNewTeamMemberAdded);
  }
}