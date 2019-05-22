
var count = 5; // Variable to count number of counts.

function validate() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if (username == "admin" && password == "password") {
        alert("Login successful");
        window.location = "success.html"; // Redirecting to other page.
        return false;
    }
    else {
        count--;// decrease variable count by 1
        alert("Invalid username or password. You have left " + count + " count");
        // Disabling username, password and submit after 5 counts.
        if (count <= 0) {
            document.getElementById("username").disabled = true;
            document.getElementById("password").disabled = true;
            document.getElementById("submit").disabled = true;
            return false;
        }
    }
}

function register() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var email = document.getElementById("email").value;

    // var fs = require("fs");
    // var userr = {
    //     'username': username,
    //     'password': password,
    //     'email': email
    // };

    // fs.writeFile("./data.json", JSON.stringify(userr, null, 3), (err) => {
    //     if (err) {
    //         console.error(err);
    //         return;
    //     };

    // });


    if (true) {
        alert("Registration successful");
        window.location = "index.html"; // Redirecting to other page.
        return false;
    }

}
function Request(){
var HttpClient = function(){
    this.get = function(aUrl, aCallback){
        var anHttpRe = new XMLHttpRequest();
        anHttpRe.onreadystatechange = function(){
            if (anHttpRe.readyState == 4 && anHttpRe.status == 200)
                aCallback(anHttpRe.responseText);
        }

        anHttpRe.open( "GET",aUrl,true );
        anHttpRe.send( null );
    }
}
    if(true){
    var URLForJsonTest = 'https://jsonplaceholder.typicode.com/todos/1';
    var Ct = new HttpClient();
        Ct.get(URLForJsonTest, function(response){
            alert(response);
        });
    }
}
