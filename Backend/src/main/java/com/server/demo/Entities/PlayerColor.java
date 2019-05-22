package com.server.demo.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="player_color")
public class PlayerColor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    @ManyToOne
    @JoinColumn(name = "session")
    private Session session;
    
    private String username;
    
    private String color;
    
    //Default Constructor
    protected PlayerColor() {}
    
    //Constructor
    public PlayerColor(Session session, String username)
    {
    	this.session = session;
    	this.username = username;
    	//NOTE - 000001 is the default value, used to indicate no color has been selected
    	this.color = "#000001";
    }
    
    public PlayerColor(Session session, String username, String color)
    {
    	this(session, username);
    	this.color = color;
    }
    
    //Session - Shouldn't get used
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    
    //Username
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    //Color
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

}
