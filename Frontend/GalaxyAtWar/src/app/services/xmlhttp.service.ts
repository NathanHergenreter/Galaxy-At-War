import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class XmlhttpService {

  createCORSRequest(method, url, user) {
    var xhr = new XMLHttpRequest();

    if ("withCredentials" in xhr) {
  
      // Check if the XMLHttpRequest object has a "withCredentials" property.
      // "withCredentials" only exists on XMLHTTPRequest2 objects.
    xhr.onload = function(){
        var state = this.readyState;
        var responseCode = xhr.status;
        console.log("Ready state: " + state + " code "  + responseCode);
        if (state == this.DONE && responseCode == 200){
            var responseData = this.responseText;
            // alert("SUCCESS" + responseData);
        }
    }   

    xhr.onerror = function(e){
        console.log('Login Error ' + e);
    }

    xhr.onreadystatechange = function(){
        console.log("ready state = " + this.readyState);
    }

    xhr.open(method, url, false);
    //xhr.setRequestHeader('Access-Control-Allow-Origin', '*')
    xhr.setRequestHeader("Content-Type", "application/json;");
    xhr.setRequestHeader('Access-Control-Allow-Credentials', 'true');
    xhr.setRequestHeader("Access-Control-Allow-Methods", "POST,GET");
    xhr.send(JSON.stringify(user));
    
    } else if (typeof XMLHttpRequest != "undefined") {
  
      // Otherwise, check if XDomainRequest.
      // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
      xhr = new XMLHttpRequest();
      xhr.open(method, url, false);
  
    } else {
  
      // Otherwise, CORS is not supported by the browser.
      xhr = null;
  
    }
    return xhr;
  }

  constructor() { }
}
