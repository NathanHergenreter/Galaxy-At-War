import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SigninComponent } from './UIcomponents/signin/signin.component';
import { CreateAccountComponent } from './UIcomponents/create-account/create-account.component';
import { gameDiscoveryLobbyComponent } from './UIcomponents/game-discovery-lobby/game-discovery-lobby.component';
import { GameConfigLobbyComponent } from './UIcomponents/game-config-lobby/game-config-lobby.component';
import { ProfileComponent } from './UIcomponents/profile/profile.component';
import { SocketsComponent } from './UIcomponents/sockets/sockets.component';
import { GameDashboardComponent } from './UIcomponents/game-dashboard/game-dashboard.component';
import { ChatComponent } from './UIcomponents/chat/chat.component';
import { GamePregameComponent } from './UIcomponents/game-pregame/game-pregame.component';

const routes: Routes = [
  {path: 'signin', component: SigninComponent},
  {path: 'create-account', component: CreateAccountComponent},
  {path: 'game-discovery-lobby', component: gameDiscoveryLobbyComponent},
  {path: 'game-config-lobby', component: GameConfigLobbyComponent},
  {path: 'game-dashboard', component: GameDashboardComponent},
  {path: 'profile', component: ProfileComponent},
  {path: 'socket', component: SocketsComponent},
  {path: 'game-pregame', component: GamePregameComponent},
  { path: '',   redirectTo: '/signin', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

export const routingComponents = [SigninComponent, CreateAccountComponent,gameDiscoveryLobbyComponent, ProfileComponent,GameConfigLobbyComponent, SocketsComponent, GameDashboardComponent, GamePregameComponent];
