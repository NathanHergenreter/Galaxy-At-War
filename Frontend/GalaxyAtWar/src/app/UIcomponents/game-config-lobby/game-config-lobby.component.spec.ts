import { async, ComponentFixture, TestBed, fakeAsync } from '@angular/core/testing';

import { GameConfigLobbyComponent } from './game-config-lobby.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { GameConfigService } from 'src/app/services/game-config.service';
import { UserSessionService } from 'src/app/services/user-session.service';
import { By } from '@angular/platform-browser';

describe('GameConfigLobbyComponent', () => {
  let fixture: ComponentFixture<GameConfigLobbyComponent>;
  let component: GameConfigLobbyComponent;

  beforeEach(fakeAsync(() => {
    const userServiceMock = jasmine.createSpyObj('UserSessionService', ['login']);
    userServiceMock.login.and.returnValue( Promise.resolve(0) );
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [GameConfigLobbyComponent],
      providers:    [
        { provide: GameConfigService },
        { provide: UserSessionService, useValue: userServiceMock}
      ]
       }).compileComponents().then(() => {
      fixture = TestBed.createComponent(GameConfigLobbyComponent);
      component = fixture.componentInstance;
    });
  }));

  it('should fire event when startpoint input value change', fakeAsync(() => {
    spyOn(component, 'onStartPointInput');
    
    let button = fixture.debugElement.query(By.css('.startpoint-input'))
    button.nativeElement.value = 'pass';
    button.nativeElement.dispatchEvent(new Event('input'));
 
    fixture.whenStable().then(() => {
      expect(component.onStartPointInput).toHaveBeenCalled();
    });
  }));
});
