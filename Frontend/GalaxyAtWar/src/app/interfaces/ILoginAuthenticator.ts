import { IProfileModel, LoginModel } from './IUserModel';
import { ServerMessages } from './EnumTypes';

export interface ILoginAuthenticator{

    /* We need some kind of information to identify user (security token/cookie/etc). 
    * We must give profile info to user who have access to that info. */
    
    login(user: LoginModel) : Promise<ServerMessages>;

    createAccount(user: LoginModel) : Promise<ServerMessages>;
}
