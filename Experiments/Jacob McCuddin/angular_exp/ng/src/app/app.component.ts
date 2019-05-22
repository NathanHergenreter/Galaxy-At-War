import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'ng';

  readonly ROOT_url = 'https://jsonplaceholder.typicode.com/todos/1';
  _http;
  posts: any;

  constructor(private http: HttpClient){
    this._http = http;
  }

  getPosts(){
    this.posts = this._http.get(this.ROOT_url + '/posts');
  }
}
