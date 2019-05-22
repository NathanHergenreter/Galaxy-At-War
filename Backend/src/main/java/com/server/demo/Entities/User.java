package com.server.demo.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
    @Column(nullable = false)
	private String name;

    @Column(nullable = false)
	private String password;
    
    @Column(nullable = false)
    private boolean admin;
    
    /*0 - Offline
     *1 - Online
     *2 - Away
     *3 - Busy
     */
    @Column(nullable = false)
    private int status;

    @Cascade(CascadeType.ALL)
    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @ManyToMany
    @JoinTable(name="friendslist",
     joinColumns=@JoinColumn(name="userId"),
     inverseJoinColumns=@JoinColumn(name="friendId")
    )
    private List<User> friends = new ArrayList<>();

    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @ManyToMany(mappedBy = "friends")
    private List<User> friendOf = new ArrayList<>();

    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "sender")
    private List<Messages> sentMessages = new ArrayList<>();

    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "receiver")
    private List<Messages> receivedMessages = new ArrayList<>();
    
    @Cascade(CascadeType.ALL)
    @OneToOne
    @JoinColumn(name = "host_session_id", referencedColumnName = "id")
    private Session hostSession;
    

    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @ManyToOne
    @JoinColumn(name = "guest_session_id", referencedColumnName = "id")
    private Session guestSession;
    
    //Note: The default constructor is required by JPA
    public User() {}

    //Use this constructor to save instances of User to be saved to the database
    public User(String name, String password) {
    	this.name = name;
    	this.password = password;
    	this.admin = false;
    	this.profile = new Profile(this);
    	this.status = 0;
    }

    //TODO fix CORS issue... eventually
    //AuthToken
    public Integer authToken;
    public void setAuthToken(Integer authToken){
        this.authToken = authToken;
    }

    public Integer getAuthToken(){
        return this.authToken;
    }

    //Name
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    //Password
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    //Admin Status
    public boolean getAdmin() { return this.admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    
    //Online Status
    public int getStatus() { return this.status; }
    public void setStatus(int status) { this.status = status; }
    
    //Profile
    public Profile getProfile() { return this.profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    //Friendslist
    public List<User> getFriends() { return this.friends; }
    public List<User> getFriendOf() { return this.friendOf; }
    public void addFriend(User friend) 
    {
    	friends.add(friend);
    	friend.getFriendOf().add(this);
    }
    public void removeFriend(User friend) 
    {
    	friends.remove(friend); 
    	friend.getFriendOf().remove(this);
    }
    //Deletes friend statuses other users have set with this user
    public void removeFriendsOf()
    {
    	//Goes through friendOf list, removes this user as a friend from their friend list
    	while(friendOf.size()!=0)
    	{
    		friendOf.get(0).removeFriend(this);
    	}
    }
    
    //Sent Messages
    public List<Messages> getSentMessages() { return sentMessages; }
    //Deletes sent messages
    public void deleteSentMessages()
    {
    	//Goes through sentMessages list, removes message from both user's sent list
    	//and receiver's received list
    	while(sentMessages.size()!=0)
    	{
    		deleteMessage(sentMessages.get(0));
    	}
    }
    public void sendMessage(User receiver, String message, Date date)
    {
        Messages m = new Messages(this, receiver, message, date);
    	sentMessages.add(m);
    	receiver.getReceivedMessages().add(m);
    }
    //Removes message from user's sentMessages and receiver receivedMessages
    public void deleteMessage(Messages message)
    {
    	sentMessages.remove(message);
    	message.getReceiver().getReceivedMessages().remove(this);
    	message.setSender(null);
    	message.setReceiver(null);
    }
    
    //Received Messages
    public List<Messages> getReceivedMessages() { return receivedMessages; }
    
    //Session
    //User should only be in one Session
    //Checks if either currently host or guest
    public Session getSession()
    {
    	//Looks if hosting a session
    	if(hostSession!=null) { return hostSession; }
    	
    	//Looks if guest in a session
    	if(guestSession!=null) { return guestSession; }
    	
    	//Otherwise, not in session
    	return null;
    }
    
    public void setHostSession(Session session) { this.hostSession = session; }
    //public void endHostSession() { this.hostSession = null; } //Needed?
    public void setGuestSession(Session session) { this.guestSession = session; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void setFriendOf(List<User> friendOf) {
        this.friendOf = friendOf;
    }

    public void setSentMessages(List<Messages> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public void setReceivedMessages(List<Messages> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public Session getHostSession() {
        return hostSession;
    }

    public Session getGuestSession() {
        return guestSession;
    }
}
