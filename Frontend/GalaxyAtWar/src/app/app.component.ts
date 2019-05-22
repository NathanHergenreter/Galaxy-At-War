import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
  constructor(router: Router){
    this._router = router;
  }
  _router:Router;
  title = 'GalaxyAtWar';
  
  onCreateAccountClick(){
    this._router.navigateByUrl('/create-account');
  }
}
