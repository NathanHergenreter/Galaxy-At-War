package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.Arrays;

//Object through which the player takes possession in the game
//Contains:
//	- the owning player's username and color
//	- lists of all the unit templates
//	- lists of owned armies, fleets, and planets
//	- current state of resources
public class Faction {

	private long id;
	private Game game;
	private String playername;
	private String color;
	@JsonBackReference
	private ArrayList<LandUnitTemplate> landTemplates = new ArrayList<LandUnitTemplate>();
	@JsonBackReference
	private ArrayList<NavalUnitTemplate> navalTemplates = new ArrayList<NavalUnitTemplate>();
	@JsonBackReference
	private ArrayList<Army> armies = new ArrayList<Army>();
	@JsonBackReference
	private ArrayList<Fleet> fleets = new ArrayList<Fleet>();
	@JsonBackReference
	private ArrayList<Planet> planets = new ArrayList<Planet>();
	private int money;
	private int manpower;
	private int alloys;
	private int armyCount;	//Highest number of armies created
	private ArrayList<String> storedArmyNames = new ArrayList<String>();
	private int fleetCount;	//Highest number of fleets created
	private ArrayList<String> storedFleetNames = new ArrayList<String>();
	
	//Constructor
	public Faction(long id, Game game, String playername, String color, int money)
	{
		this.id = id;
		this.game = game;
		this.playername = playername;
		this.color = color;
		this.money = money;
		this.manpower = 0;
		this.alloys = 0;
		this.armyCount = 0;
		this.fleetCount = 0;
	}
	
	//Player Info
	public long getID() { return id; }
	public Game game() { return game; }
	public String getPlayer() { return playername; }
	public String getColor() { return color; }
	
	//Land Templates
	public ArrayList<LandUnitTemplate> getLandTemplates() { return landTemplates; }
	public LandUnitTemplate getLandTemplate(String type)
	{
		//Searches through list of templates for requested type
		for(LandUnitTemplate t : landTemplates)
		{
			if(t.getType().equals(type))
			{
				return t;
			}
		}
		
		//Requested type was not found, returns default
		return landTemplates.get(0);
	}
	public void addLandTemplate(LandUnitTemplate temp) { landTemplates.add(temp); }

	//Naval Templates
	public ArrayList<NavalUnitTemplate> getNavalTemplates() { return navalTemplates; }
	public NavalUnitTemplate getNavalTemplate(String type)
	{
		//Searches through list of templates for requested type
		for(NavalUnitTemplate t : navalTemplates)
		{
			if(t.getType().equals(type))
			{
				return t;
			}
		}
		
		//Requested type was not found, returns default
		return navalTemplates.get(0);
	}
	public void addNavalTemplate(NavalUnitTemplate temp) { navalTemplates.add(temp); }
	
	//Armies
	public ArrayList<Army> getArmies() { return armies; }
	public void addArmy(Army army) { armies.add(army); }
	public void removeArmy(Army army) { armies.remove(army); army.removeOwner(); }
	
	//Fleets
	public ArrayList<Fleet> getFleets() { return fleets; }
	public void addFleet(Fleet fleet) { fleets.add(fleet); }
	public void removeFleet(Fleet fleet) { fleets.remove(fleet); fleet.removeOwner(); }
	
	//Planets
	public ArrayList<Planet> getPlanets() { return planets; }
	public void addPlanet(Planet planet) { planets.add(planet); }
	public void removePlanet(Planet planet) { planets.remove(planet); }
	
	//Resources
	public void modResAll(int money, int manpower, int alloys)
	{
		modMoney(money);
		modManpower(manpower);
		modAlloys(alloys);
	}
	public int getMoney() { return money; }
	public void modMoney(int mod) 
	{ 
		this.money += mod;
		
		//Faction id || money mod
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("modMoney", ids, mod));
	}
	public int getManpower() { return manpower; }
	public void modManpower(int mod) 
	{ 
		this.manpower += mod; 
		
		//Faction id || manpower mod
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("modManpower", ids, mod));
	}
	public int getAlloys() { return alloys; }
	public void modAlloys(int mod) 
	{ 
		this.alloys += mod; 
		
		//Faction id || alloys mod
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("modAlloys", ids, mod));
	}
	
	//Next Army Name
	public String getNextArmyName()
	{
		//If there is a stored army name, removes from storage list and returns it
		if(storedArmyNames.size()>0)
		{
			String temp = storedArmyNames.get(0);
			storedArmyNames.remove(0);
			return temp;
		}
		else
		{
			String ret = "";
			
			//Increments army count, adds to string
			armyCount++;
			ret += Integer.toString(armyCount);
			
			//Appends "st", "nd", "rd", or "th" depending on last digit of count
			char last = ret.charAt(ret.length()-1);
			switch (last)
			{
				case '1':
				{
					ret += "st ";
					break;
				}
				case '2':
				{
					ret += "nd ";
					break;
				}
				case '3':
				{
					ret += "rd ";
					break;
				}
				default:
				{
					ret += "th ";
					break;
				}
			}
			
			ret += "Army";
			return ret;
		}
	}
	
	//Store Army name
	public void storeArmyName(String name)
	{
		this.storedArmyNames.add(name);
	}
	
	//Next Fleet Name
	public String getNextFleetName()
	{
		//If there is a stored army name, removes from storage list and returns it
		if(storedFleetNames.size()>0)
		{
			String temp = storedFleetNames.get(0);
			storedFleetNames.remove(0);
			return temp;
		}
		else
		{
			String ret = "";
			
			//Increments army count, adds to string
			fleetCount++;
			ret += Integer.toString(fleetCount);
			
			//Appends "st", "nd", "rd", or "th" depending on last digit of count
			char last = ret.charAt(ret.length()-1);
			switch (last)
			{
				case '1':
				{
					ret += "st ";
					break;
				}
				case '2':
				{
					ret += "nd ";
					break;
				}
				case '3':
				{
					ret += "rd ";
					break;
				}
				default:
				{
					ret += "th ";
					break;
				}
			}
			
			ret += "Fleet";
			return ret;
		}
	}
	
	//Store Fleet name
	public void storeFleetName(String name)
	{
		this.storedFleetNames.add(name);
	}
}
