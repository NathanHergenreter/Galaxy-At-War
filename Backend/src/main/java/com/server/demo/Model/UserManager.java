package com.server.demo.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.demo.Entities.User;
import com.server.demo.Repositories.UserRepository;

@Service
public class UserManager {

	@Autowired
	UserRepository repo;

    //Gets user with given name
    //Checks if password is correct
    public User getUser(String username){

    	if (username == null) return null;
    	return repo.findByName(username);
    }
	public List<PsuedoUser> getAllUsers() {
		List<User> users = this.repo.findAll();
		List<PsuedoUser> usersDTO = new ArrayList<>();
		for (User user : users) {
			PsuedoUser u = new PsuedoUser(user.getName(), null);
			usersDTO.add(u);
		}
		return usersDTO;
	}


    //
    public User getValidatedUser(String username, String password){
    	if (username == null) return null;
    	User user = repo.findByName(username);
    	if (user==null) return null;
    	if (!user.getPassword().equals(password)) return null;
    	return user;
	}
    
	//Adds user to DB
    public boolean addUser(User user){

    	if (repo.findByName(user.getName()) != null) return false;

    	//Checks if username is already in db
    	//If so, adds to db and returns true
    	if(this.getUser(user.getName())==null)
    	{
    		repo.save(user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }

    //Removes user from DB
    public boolean deleteUser(User user){
    	User db_user = this.getUser(user.getName());
    	
    	//Checks if username is in db and if password in db matches given password
    	//If so, deletes from db and returns true
    	if(db_user!=null && db_user.getPassword().equals(user.getPassword()))
    	{
    		db_user.removeFriendsOf();
    		repo.delete(db_user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }

    //Updates user in DB
    public boolean updateUser(User user){
    	//Checks if user is in db
    	//If so, updates user
    	if(this.getUser(user.getName())!=null)
    	{
    		repo.save(user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }
    
    //Adds user to given user's friendslist
    public boolean addFriend(String username, String friendname)
    {
    	User user = this.getUser(username);
    	User friend = this.getUser(friendname);
    	
    	//Checks if:
    	//	User and friend are in db
    	//  User is not friend
    	//	Friend is not already in friendlist
    	//If so, adds friend to user's friendlist
    	if(user!=null && friend !=null && user!=friend && !user.getFriends().contains(friend))
    	{
    		user.addFriend(friend);
    		repo.save(user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }

    //Removes user from given user's friendslist
    public boolean removeFriend(String username, String friendname)
    {
    	User user = this.getUser(username);
    	User friend = this.getUser(friendname);
    	
    	//Checks if:
    	//	User and friend are in db
    	//  User is not friend
    	//	Friend is in friendlist
    	//If so, adds friend to user's friendlist
    	if(user!=null && friend !=null && user!=friend && user.getFriends().contains(friend))
    	{
    		user.removeFriend(friend);
    		repo.save(user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }

    //Updates password
    public boolean updatePassword(String username, String password){
		User user = this.getUser(username);
		if (user == null) return false;

		user.setPassword(password);
		repo.save(user);
		return true;
	}

    //Updates user online status
    public boolean updateStatus(String username, int status){
    	User user = this.getUser(username);
    	if (user == null) return false;

    	user.setStatus(status);
    	repo.save(user);
    	return true;
	}

    // Updates user bio
	public boolean updateBio(String oldUsername, String username, String bio) {
		// Checks if user is in db
		// If so, updates user bio
		User user = this.getUser(oldUsername);
		if (user != null) {
			user.getProfile().setBio(bio);
			user.setName(username);
			repo.save(user);
			return true;
		}
		// Otherwise returns false
		return false;
	}

    //Updates user avatar path
    public boolean updateAvatar(String username, String path){
    	//Checks if user is in db
    	//If so, updates user avatar path
    	User user = this.getUser(username);
    	if(user!=null)
    	{
    		user.getProfile().setAvatarPath(path);
    		repo.save(user);
            return true;
    	}
    	//Otherwise returns false
    	else
    	{
            return false;
    	}
    }
}
