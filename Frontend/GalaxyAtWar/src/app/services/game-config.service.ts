import { Injectable } from '@angular/core';
import { IGameConfig } from '../interfaces/IGameConfig';
import { IGameConfigModel } from '../interfaces/IGameConfigModel';
import { ServerMessages } from '../interfaces/EnumTypes';
import { HttpClient } from '@angular/common/http';
import { XmlhttpService } from 'src/app/services/xmlhttp.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { UserSessionService } from './user-session.service';

@Injectable({
    providedIn: 'root'
})
export class GameConfigService implements IGameConfig {

    constructor(private _httpClient: HttpClient,private _xmlhttp: XmlhttpService,private _userSession: UserSessionService) { }

    private gameClient;

    //server host paths
    //-----------------
    private GAME_CONFIG_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/game/games";
    private INIT_GAME_CONFIG_URL: string = "http://cs309-vc-1.misc.iastate.edu:8080/game/initgame";

    //local host paths
    //----------------
    //private GAME_CONFIG_URL: string = "http://localhost:8080/game/games";
    //private INIT_GAME_CONFIG_URL: string = "http://localhost:8080/game/initgame";

    async setGameConfigAsync(config: IGameConfigModel): Promise<number> {
        try {
              var xhr = await this._xmlhttp.createCORSRequest('POST', this.INIT_GAME_CONFIG_URL+
              '?hostname='+config.hostUsername+
              '&gamename='+config.gameName+
              '&numplayers='+config.playerCount+
              '&numplanets='+config.planetCount+
              '&startmoney='+config.startingMoney+
              '&startpoints='+config.startPoint, null);
              if (!xhr) {
                  throw new Error('CORS not supported');
              }
            return parseInt(xhr.responseText);
        }
        catch (error) {
            return 0;
        }
    }

    async getActiveGameConfigsAsync(): Promise<any[]> {
        try {
            var xhr = await this._xmlhttp.createCORSRequest('GET', this.GAME_CONFIG_URL, null);
            if (!xhr) {
                throw new Error('CORS not supported');
            }
        return JSON.parse(xhr.response);
      }
      catch (error) {
          return this.convertToJSON(null);
      }
    }

    private convertToJSON(response: string):any[]{
        let result =     [
            {
                playerNeeded: '2',
                config:
                {
                  gameName: 'TheBestGroup',
                  playerCount: 1,
                  planetCount: 15,
                  startingMoney: 200,
                  startPoint: 200
                }
            },
            {
                playerNeeded: '3',
                config:
                {
                  gameName: 'JacobsGroup',
                  playerCount: 2,
                  planetCount: 30,
                  startingMoney: 200,
                  startPoint: 200
                }
            },
            {
                playerNeeded: '4',
                config:
                {
                  gameName: 'YourMommasGroup',
                  playerCount: 2,
                  planetCount: 25,
                  startingMoney: 200,
                  startPoint: 200
                }
            },
            {
                playerNeeded: '5',
                config:
                {
                  gameName: 'DerpGroup',
                  playerCount: 2,
                  planetCount: 60,
                  startingMoney: 200,
                  startPoint: 200
                }
            },
        ];
        return result;
    }
}
