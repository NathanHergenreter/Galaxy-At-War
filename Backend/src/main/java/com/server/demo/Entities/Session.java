package com.server.demo.Entities;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;



//Contains Settings for to-be-made game
//Client pulls these from the server and populates the search lobby with them
//Used in instantiating a new instance of Game

@Entity
@Table(name="session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    @OneToOne(mappedBy = "hostSession")
    private User host;

    @OneToMany(mappedBy = "guestSession")
    private List<User> guests = new ArrayList();
    
	private String name;

	private int playerCount;

	private int planetCount;
	
	private int startMoney;
	
	private int startPoints;

	private int readyCount;

    //Websocket temporary session id
    String sockId = null;

    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "session")
	private List<PlayerColor> playerColors = new ArrayList();

    //Default Constructor
    protected Session() {}
    
    //Constructor
    //Takes a list of settings (and the creator/host)
    //Adds the host to list of player colors
    public Session(User host, String name, int playerCount, int planetCount, 
    				int startMoney, int startPoints)
    {
    	this.host = host;
    	this.name = name;
    	this.playerColors.add(new PlayerColor(this, host.getName(), "#283AD6"));
    	this.playerCount = playerCount;
    	this.planetCount = planetCount;
    	this.startMoney = startMoney;
    	this.startPoints = startPoints;
    	this.readyCount = 1;
    }
    
    //ID
    public long getID() { return id; }
    
    //Host
    public User getHost() { return host; }
    public void setHost(User host) { this.host = host; } //Shouldn't get used
    
    //Session Name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    //Player Count
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; } //Shouldn't get used
    
    //Planet Count
    public int getPlanetCount() { return planetCount; }
    public void setPlanetCount(int planetCount) { this.planetCount = planetCount; } //Shouldn't get used
    
    //Starting Money
    public int getStartingMoney() { return startMoney; }
    public void setStartingMoney(int startMoney) { this.startMoney = startMoney; } //Shouldn't get used
    
    //Startpos Points
    public int getStartingPoints() { return startPoints; }
    public void setStartingPoints(int startPoints) { this.startPoints = startPoints; }
    
    //Ready Count
    public int getReadyCount() { return readyCount; }
    public void readyPlayer() { readyCount++; }
    public void unreadyPlayer() { readyCount--; }

    //Ends Session, removes all users
    public void endSession()
    {
    	while(guests.size() > 0) { removeGuest(guests.get(0)); }

    	host.setHostSession(null);
    	playerColors.remove(getPlayerColor(host));
    	setHost(null);
    }
    
    //Guests
    public List<User> getGuests() { return guests; }
    public User getGuests(int i) { return guests.get(i); }
    //Adds new player to guests list
    //Adds a new playerColor with default color value for new player
    public void addGuest(User player)
    {
    	player.setGuestSession(this);
    	guests.add(player);
    	this.readyCount++;
    	
    	String color = "A9AEBA";
    	
    	switch(guests.size()) 
    	{
    		//Red
    		case 1:
    			color = "#D61919";
    			break;
			//Green
    		case 2:
    			color = "#2ED33C";
    			break;
			//Yellow
    		case 3:
    			color = "#FFE74C";
    			break;
			//Purple
    		case 4:
    			color = "#9C26B3";
    			break;
			//Orange
    		case 5:
    			color = "#F7810C";
    			break;
			//Cyan
    		case 6:
    			color = "#0CEEF7";
    			break;
			//Grey
    		default:
    			color = "A9AEBA";
    	}
    	playerColors.add(new PlayerColor(this, player.getName(), color));
    }
    //Removes a player from guests list
    //First removes their playerColor
    public void removeGuest(User player)
    {
    	player.setGuestSession(null);
    	playerColors.remove(getPlayerColor(player));
    	guests.remove(player);
    }
    
    //Find Player index
    public int getIndex(User player)
    {
    	int i;
    	
    	//Player is host
    	if(player.getName().equals(host.getName()))
    	{
    		return 0;
    	}
    	
    	//Player is a guest
    	for(i=1; i < guests.size(); i++)
    	{
    		if(player.getName().equals(guests.get(i).getName()))
    		{
    			return i;
    		}
    	}
    	
    	//Player is not in Session
    	return -1;
    }
    
    //Player Colors
    public List<PlayerColor> getColors() { return playerColors; }
    public PlayerColor getPlayerColor(User player)
    {
    	//Looks through playerColors to find player's corresponding color
    	for(PlayerColor color : playerColors)
    	{
    		if(color.getUsername().equals(player.getName()))
    		{
    			return color;
    		}
    	} 
    	
    	//Player not found in colors list
    	return null;
    }
    //Uses getPlayerColor to find the PlayerColor instance, returns the string if non-null
    public String getColor(User player) 
    {	
    	PlayerColor color = getPlayerColor(player);
    	
    	//If player's color was found, returns the hex rgb value
    	if(color!=null) { return color.getColor(); } 
    	
    	//Player not found in colors list
    	return null;
    }
    
    //Takes a user, uses getPlayerColor to find player's color, updates color with given string
    public void setColor(User player, String color) 
    {
    	//Sets the color to a new hex value
    	getPlayerColor(player).setColor(color);
    }

    public boolean allPlayersJoined(){
        return guests.size() == playerCount;
    }

    public String getSockId(){
        return sockId;
    }

    public void setSockId(String id){
        sockId = id;
    }
}
