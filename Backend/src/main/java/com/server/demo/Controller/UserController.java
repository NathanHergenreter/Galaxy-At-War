package com.server.demo.Controller;

import com.server.demo.Entities.User;
import com.server.demo.Handlers.PsuedoAuthManager;
import com.server.demo.Model.PsuedoUser;
import com.server.demo.Model.UserManager;
import com.server.demo.Repositories.UserRepository;
import com.server.demo.Service.FileStorageService;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping(value="/user")
//@CrossOrigin(origins="http://localhost:4200")
@CrossOrigin(origins="*")
public class UserController {

    PsuedoAuthManager psuedoAuthManager = new PsuedoAuthManager();
    Random random = new Random();
	
	@Autowired
	UserManager manager;
	
	@Autowired
    FileStorageService fileStorageService;

	// Returns a list of users
	@GetMapping(value = "/users")
	@ResponseBody
	public List<PsuedoUser> getUserList(@RequestParam(name = "start", required = false) Integer start,
			@RequestParam(name = "size", required = false) Integer size) {
		return this.manager.getAllUsers();
	}

	//Registers a user using given username and password
	//If username is already taken, does not add again
    @PostMapping(value="/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody User user)
    {
    	if (user == null){
            return new ResponseEntity<>("Username: "+user.getName()+" Password: "+user.getPassword(), HttpStatus.BAD_REQUEST);
        }

        //Validate user and username
        if (user.getName() == null || user.getPassword() == null){
            return new ResponseEntity<>("Missing username or password", HttpStatus.UNAUTHORIZED);
        }

        if (manager.addUser(new User(user.getName(), user.getPassword())) == false){
            //I know this isn't technically the right thing to use, buuuut...
            return new ResponseEntity<>("User already exists", HttpStatus.I_AM_A_TEAPOT);
        }
    	
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    //Unregisters a user, removes from db
    //If user does not exist, no remove
    @PostMapping(value="/unregister")
    @ResponseBody
    public ResponseEntity<?> unRegisterUser(HttpSession session, @ModelAttribute User maUser)
    {
        User user;
        if (maUser == null || (user = manager.getUser(maUser.getName())) == null){
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        session.invalidate();

    	if (!manager.deleteUser(user)){
            return new ResponseEntity<>("Bad username or password", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }
    
    //Logins in a user, gives session to user
    //If user does not exist or password is incorrect, sends error
    @PostMapping(value="/login")
    @ResponseBody
    public ResponseEntity<?> loginUser(HttpSession session, @RequestBody User maUser)
    {
    	User user = manager.getValidatedUser(maUser.getName(), maUser.getPassword());
    	PsuedoUser psuedoUser = new PsuedoUser(maUser.getName(), random.nextInt());

    	System.out.println(">>>");
        System.out.println(maUser.getName());
    	
    	if(user==null) {
            return new ResponseEntity<>("Bad username (" + maUser.getName()+") or password ("+maUser.getPassword()+")", HttpStatus.BAD_REQUEST);
        }
        String test = (String)session.getAttribute("user_name");
        session.setAttribute("logged_in", true);
    	session.setAttribute("user_name", user.getName());

        user.setStatus(1);
        return new ResponseEntity<>(Integer.toString(psuedoUser.authToken), HttpStatus.OK);
    }

    //Logouts out the user with the current session, ends session
    @GetMapping(value="/logout")
    @ResponseBody
    public ResponseEntity<?> logoutUser(
            HttpSession session,
            @RequestParam(name="authtoken") Integer authToken){
        String username = (String) session.getAttribute("user_name");
        //String username = psuedoAuthManager

        if (username == null){
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

        session.invalidate();
        User user = manager.getUser(username);
        user.setStatus(0);


        return new ResponseEntity<>("", HttpStatus.OK);
    }

    //Returns the username of the user holding the current session
    @GetMapping(value="/username")
    @ResponseBody
    public String getCurrentUsername(HttpSession session){
        return (String) session.getAttribute("user_name");
    }
    
	//Adds a user to current session user's friendslist
	//If friendname does not exist, returns error
    @GetMapping(value="/befriend")
    @ResponseBody
    public ResponseEntity<?> addFriend(HttpSession session,
                              @RequestParam(name="friendname") String friendname)
    {
        String username = (String)session.getAttribute("user_name");
        if (username == null) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

    	if (!manager.addFriend(username, friendname)){
            return new ResponseEntity<>("Unable to add friend", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

	//Removes a user from current session user's friendslist
	//If friendname does not exist, returns error
    @GetMapping(value="/unfriend")
    public ResponseEntity<?> removeFriend(HttpSession session,
                                @RequestParam(name="friendname") String friendname)
    {

        String username = (String) session.getAttribute("user_name");
        if (username == null) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

    	if (!manager.removeFriend(username, friendname)){
            return new ResponseEntity<>("Unable to remove friend", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>("", HttpStatus.OK);
    }
    
    //Returns the friends of the user with the given username
    @GetMapping(value="/friends")
    @ResponseBody
    public List<User> getFriends(String username){

        return manager.getUser(username).getFriends();
    }


    // Updates the given current session user's bio
    @PostMapping(value = "/profile/bio")
    @ResponseBody
    public ResponseEntity<?> updateBio(HttpSession session, @RequestParam(name = "bio") String bio,
                                       @RequestParam(name = "username") String username, @RequestParam(name = "oldUsername") String oldUsername) {

        if (!manager.updateBio(oldUsername, username, bio)) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // Returns the bio of the user with the given username
    @GetMapping(value = "/profile/bio")
    @ResponseBody
    public String getBio(HttpSession session, @RequestParam(name = "username") String username) {
        User user = manager.getUser(username);
        if (user == null) {
            return "";
        }

        return user.getProfile().getBio();
    }

    //Downloads the avatar of the given user
    @GetMapping(value="/profile/avatar")
    public ResponseEntity<Resource> downloadAvatar(@RequestParam("username") String username
                                                ,HttpServletRequest request){

        User user = manager.getUser(username);
        String imgName = username;
        if (user == null){
            imgName = "default.png";
        } else {
            imgName = user.getProfile().getAvatarPath();
        }

        Resource resource;

        try {
            resource = fileStorageService.loadFileAsResource(imgName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound()
                    .build();
        }

        String contentType = null;
        try{
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e){
        }

        if (contentType == null){
            contentType="application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename\"" + resource.getFilename() + "\"")
                .body(resource);
    }


	//Updates the avater of the current session user 
    @PostMapping(value="/profile/avatar")
    public ResponseEntity<?> updateAvatar(HttpSession session,
                                          @RequestParam(name ="file") MultipartFile file)
    {
        String username = (String) session.getAttribute("user_name");
        if (username == null){
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

        String filename = fileStorageService.storeFile(file, username);

        ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("../var/www/html/avatars")
                .path(filename)
            .toUriString();

        String extension = filename.substring(filename.indexOf('.'));
        String avatarName = username+extension;

    	if (!manager.updateAvatar(username, avatarName)){
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    	
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
