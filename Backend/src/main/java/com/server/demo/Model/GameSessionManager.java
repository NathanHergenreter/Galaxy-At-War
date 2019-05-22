package com.server.demo.Model;

import com.server.demo.Entities.PlayerColor;
import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;
import com.server.demo.Repositories.SessionRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSessionManager {

	@Autowired
	SessionRepository repo;
	
	//Returns the game session with the given id
	public Session getSession(long id)
	{
		Optional<Session> session = repo.findById(id);
		if(session.isPresent()) { return session.get(); }
		else { return null; }
	}
	
	//Returns game session with given host and name
	public Session getSession(User host, String name)
	{
		Session session = repo.findByHostAndName(host, name);
		return session;
	}
	
	//Returns all the game sessions
	public List<Session> getAllSessions()
	{
		return repo.findAll();
	}
	
	//Adds a game session to the db
	public boolean addSession(Session session)
	{
		repo.save(session);
        return true;
	}

    //Removes user from DB
    public boolean deleteSession(Session session){
    	Session dbSession = this.getSession(session.getID());
    	
    	if(dbSession!=null)
    	{
    		dbSession.endSession();
    		repo.delete(dbSession);
            return true;
    	}
    	else { return false; }
    }

    //Adds user to given session's guest list
    public boolean addGuest(Session session, User guest)
    {
    	Session dbSession = this.getSession(session.getID());
    	
    	if(dbSession!=null && guest !=null  && !dbSession.getGuests().contains(guest))
    	{
    		dbSession.addGuest(guest);
    		repo.save(dbSession);
            return true;
    	}
    	else { return false; }
    }

    //Removes user from given session's guest list
    public boolean removeGuest(Session session, User guest)
    {
    	Session dbSession = this.getSession(session.getID());
    	
    	if(dbSession!=null && guest !=null  && dbSession.getGuests().contains(guest))
    	{
    		dbSession.removeGuest(guest);
    		repo.save(dbSession);
            return true;
    	}
    	else { return false; }
    }
    
    //Sets a user's color
    public boolean setColor(Session session, User player, String color)
    {
    	Session dbSession = this.getSession(session.getID());
    	PlayerColor pColor = null;
    	
    	if(dbSession != null && player != null) { pColor = dbSession.getPlayerColor(player); }
    	
    	if(pColor != null) { pColor.setColor(color); return true; }
    	else { return false; }
    }
    
    //Increments ready count
    public boolean playerReady(Session session)
    {
    	Session dbSession = this.getSession(session.getID());
    	
    	if(dbSession != null) { dbSession.readyPlayer(); return true; }
    	else { return false; }
    }
    
    //Decrements ready count
    public boolean playerUnready(Session session)
    {
    	Session dbSession = this.getSession(session.getID());
    	
    	if(dbSession != null) { dbSession.unreadyPlayer(); return true; }
    	else { return false; }
    }
}
