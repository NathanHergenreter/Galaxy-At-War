import { IGameConfigModel } from './IGameConfigModel';
import { ServerMessages } from './EnumTypes';

export interface IGameConfig {

    /* We need some kind of information to identify user (security token/cookie/etc). 
    * We must give profile info to user who have access to that info. */

    setGameConfigAsync(config: IGameConfigModel): Promise<number>;
}
