import { Component, OnInit } from '@angular/core';
import { User } from '../user';
import { ApiHttpService } from '../api-http.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [ApiHttpService]
})
export class LoginComponent implements OnInit {

  user: User = new User();
  error: any;

  constructor(private apiHttpService: ApiHttpService, private router: Router) { }

  ngOnInit() {
    console.log(this.user);
  }

  public submit() {
    console.log("Send data:", this.user);

    this.apiHttpService.login(this.user)
      .subscribe(
        (resp: User) => { 
          if(resp.success) {
            console.log("Login OK");
            

            localStorage.setItem("user", JSON.stringify(resp));
            
            
          } else {
            console.log("Login Failure. Server responce: ", resp);
            this.error = {
              message: "Login failure. " + ( resp.message != undefined ? resp.message : "")
            };
          }
        },
        error => {
          console.log(error)
          this.error = error;
        }
      );
  }

}

