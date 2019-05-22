import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from './user'

import { from } from 'rxjs';

@Injectable()
export class ApiHttpService {

  
  LOGIN_URL: string = "http://localhost/api/login.php";
  REGISTER_URL: string = "http://localhost/api/register.php";

  constructor(private http: HttpClient) { }

  public login(user: User) {
    return this.http.post(this.LOGIN_URL, user);
  }

  public register(user: User) {
    return this.http.post(this.REGISTER_URL, 
      { 
        name: user.name,
        password: user.password,
        
      }
    );
  }

}
