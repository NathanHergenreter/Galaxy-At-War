import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';

import { AppRoutingModule, routingComponents } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { LoginAuthenticatorService } from './services/login-authenticator.service';
import { ProfileUpdaterService } from './services/profile-updater.service';
import { UserSessionService } from './services/user-session.service';
import { GameConfigService } from './services/game-config.service';
import { ChatComponent } from './UIcomponents/chat/chat.component';
import { SocketsComponent } from './UIcomponents/sockets/sockets.component';

@NgModule({
  declarations: [
    AppComponent,
    routingComponents,
    ChatComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    LoginAuthenticatorService,
    ProfileUpdaterService,
    UserSessionService,
    GameConfigService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
