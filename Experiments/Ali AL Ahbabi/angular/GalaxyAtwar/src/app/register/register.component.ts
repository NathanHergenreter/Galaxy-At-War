import { Component, OnInit } from '@angular/core';
import { ApiHttpService } from '../api-http.service';
import { User } from '../user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  user: User = new User();
  password1: string;
  password2: string;
  error: any;

  constructor(private apiHttpService: ApiHttpService, private router: Router) { }

  ngOnInit() { }

  public submit() {
    if(this.user.name == undefined || this.user.name.trim().length == 0) {
      this.error = { message: 'Username required' };
    }  else if(this.password1 != this.password2) {
      this.error = { message: 'Passwords does not match' };
      this.user.password = this.password1;
    } else {
      this.apiHttpService.register(this.user).subscribe(
        (resp: User) => { 
          if(resp.success) {
            console.log("Register OK");
            this.router.navigate(['/profile']);

          } else {
            console.log("Register Failure. Server responce: ", resp);
            this.error = {
              message: "Register failure. Reason: " + ( resp.message != undefined ? resp.message : "")
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

}
