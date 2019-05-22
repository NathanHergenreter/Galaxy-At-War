import { async, ComponentFixture, TestBed, fakeAsync } from '@angular/core/testing';

import { CreateAccountComponent } from './create-account.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { LoginAuthenticatorService } from 'src/app/services/login-authenticator.service';
import { UserSessionService } from 'src/app/services/user-session.service';
import { By } from '@angular/platform-browser';
import { CssSelector } from '@angular/compiler';


// Main test case
describe('CreateAccountComponent', () => {
  let fixture: ComponentFixture<CreateAccountComponent>;
  let component: CreateAccountComponent;

  // initialazing our test cases
  beforeEach(fakeAsync(() => {
    // providing mock of LoginAuthenticatorService and UserSessionService
    const authServiceMock = jasmine.createSpyObj('LoginAuthenticatorService', ['login']);
    const userServiceMock = jasmine.createSpyObj('UserSessionService', ['login']);
    // Mocking the login function of the LoginAuthenticatorService
    authServiceMock.login.and.returnValue( Promise.resolve(0) );
    // Injecting the mocked services in the Component we want to test which is in this case CreateAccountComponent 
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [CreateAccountComponent],
      providers:    [
        { provide: LoginAuthenticatorService, useValue: authServiceMock },
        { provide: UserSessionService, useValue: userServiceMock}
      ]
       }).compileComponents().then(() => {
      fixture = TestBed.createComponent(CreateAccountComponent);
      component = fixture.componentInstance;
    }); 
  }));

  // First child test case, we can have multiple ones here
  it('should fire event when password input value change', fakeAsync(() => {
    // watch on the 'onPasswordInput' function, because it's the function that we want to trigger.
    spyOn(component, 'onPasswordInput');
    
    // Grap the element with the class='.password-input', and then change its value to 'pass' so we check if the event get fired and the 'onPasswordInput' function get called.
    let button = fixture.debugElement.query(By.css('.password-input'))
    button.nativeElement.value = 'pass';
    button.nativeElement.dispatchEvent(new Event('input'));
 
    // Assert that the function is called
    fixture.whenStable().then(() => {
      expect(component.onPasswordInput).toHaveBeenCalled();
    });
  }));

});
