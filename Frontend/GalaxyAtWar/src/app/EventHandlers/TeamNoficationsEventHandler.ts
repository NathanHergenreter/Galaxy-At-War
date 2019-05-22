import { IBasicEvent } from '../interfaces/IBasicEvent';
import { BasicEvent } from '../EventHandlers/BasicEvent';

export class TeamNotifcationsEventHandler {
    private readonly OnNewTeamMemberAdded = new BasicEvent();
    private readonly OnTeamMemberColorChanged = new BasicEvent();
    private readonly OnPlayerMoved = new BasicEvent();

    get NewTeamMemberAdded() { return this.OnNewTeamMemberAdded.expose(); }
    get TeamMemeberColorChanged() { return this.OnTeamMemberColorChanged.expose(); }
    get PlayerMoved() { return this.OnPlayerMoved.expose(); }
}