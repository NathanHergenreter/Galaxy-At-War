package com.server.demo.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "profile")
public class Profile {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne(mappedBy = "profile")
	private User user;

	private String bio;

	private String avatar_path;

	private int played;

	private int won;
	
	//Needed for JPA
	protected Profile() {}
	
	//Constructor to be used
	public Profile(User user)
	{
		this.user = user;
		this.bio = "This is where you tell other players all about yourself.\nFeel free to say (mostly)* anything you want!\n\n *MOSTLY - Hint Hint";
		this.avatar_path = "default.png";
		this.played = 0;
		this.won = 0;
	}
	
	//User
	public User getUser() { return this.user; }
	public void setUser(User user) { this.user = user; }
	
	//Bio
	public String getBio() { return this.bio; }
	public void setBio(String bio) { this.bio = bio; }
	
	//Avatar Filepath
	public String getAvatarPath() { return this.avatar_path; }
	public void setAvatarPath(String path) { this.avatar_path = path; }
	
	//Played
	public int getPlayed() { return this.played; }
	public void setPlayed(int played) { this.played = played; }
	public void incPlayed() { this.played++; }
	
	//Won
	public int getWon() { return this.won; }
	public void setWon(int won) { this.won = won; }
	public void incWon() { this.won++; }
}
