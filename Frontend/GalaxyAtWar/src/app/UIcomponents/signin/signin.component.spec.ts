import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';

import { SigninComponent } from './signin.component';
import { By } from '@angular/platform-browser';
import { LoginAuthenticatorService } from 'src/app/services/login-authenticator.service';
import { UserSessionService } from 'src/app/services/user-session.service';
import { Router } from '@angular/router';
import { DebugElement, ElementRef } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

// Main test case
describe('SigninComponent', () => {
  let fixture: ComponentFixture<SigninComponent>;
  let component: SigninComponent;

  
  beforeEach(fakeAsync(() => {
    const authServiceMock = jasmine.createSpyObj('LoginAuthenticatorService', ['login']);
    const userServiceMock = jasmine.createSpyObj('UserSessionService', ['updateUserSession']);
    // Mocking the login function of the LoginAuthenticatorService
    authServiceMock.login.and.returnValue(Promise.resolve(0));
    userServiceMock.updateUserSession.and.returnValue('OK');

    // Injecting the mocked services in the Component
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [SigninComponent],
      providers: [
        { provide: LoginAuthenticatorService, useValue: authServiceMock },
        { provide: UserSessionService, useValue: userServiceMock }
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(SigninComponent);
      component = fixture.componentInstance;
    });
  }));

  // Test goal : check if the onSubmit function get triggered when we click the submit button
  it('should click the submit button', fakeAsync(() => {
    // We spy on the 'onSubmit' function, because it's the function that we want to trigger.
    spyOn(component, 'onSubmit');
    // Grap the element with the class='.submit-btn', and then change its value to 'pass' so we check if the event get fired and the 'onSubmit' function get called.
    let button = fixture.debugElement.query(By.css('.submit-btn')).nativeElement;
    // Perform button click
    button.click();
    // Assert that the function gets called
    fixture.whenStable().then(() => {
      expect(component.onSubmit).toHaveBeenCalled();
    });
  }));

});
