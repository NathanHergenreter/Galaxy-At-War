package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;

//Template for a land unit
//Contains its type name and all of its stats
//Each instance of LandUnit refers back to this template to get stat info
public class LandUnitTemplate {

	private long id;
	@JsonManagedReference
	private Faction owner;
	private String type;
	private String transportType;
	private int buildTime;	//Number of days it takes to build unit
	private int cost;	//From 0-n, cost in money
	private int manpower;	//From 0-n, cost in manpower (in 10,000's of men)
	private int alloys;	//From 0-n, cost in alloys
	private int hp;	//From 1-n
	private int hardness;	//From 0-9, reduces damage taken
	private int attack;	//From 0-9, determines effectiveness in invading
	private int defense;	//From 0-9, determines effectiveness in defending against invasion
	private int boardingAttack;	//From 0-9, determines effectiveness in boarding (naval battles only)
	private int boardingDefense;	//From 0-9, determines effectiveness in defending against boarding (naval)
	private int infantryPower;	//From 0-9, determines strength/presence of unit's infantry component
	private int armorPower;	//From 0-9, determines strength/presence of unit's vehicle component
	private int artilleryPower;	//From 0-9, determines strength/presence of unit's artillery component
	private int airPower;	//From 0-9, determines strength/presence of unit's air support component
	private int count;	//Keeps track how many units of this type have been made by this faction
	private ArrayList<String> storedNames = new ArrayList<String>();
	
	//Constructor - takes in all variable data
	public LandUnitTemplate(Faction owner, String type, String transportType,
							int buildTime, int cost, int manpower, int alloys,
							int hp, int hardness,
							int attack, int defense, int boardingAttack, int boardingDefense,
							int infantryPower, int armorPower, int artilleryPower, int airPower)
	{
		this.id = -1;
		this.owner = owner;
		this.type = type;
		this.transportType = transportType;
		this.buildTime = buildTime;
		this.cost = cost;
		this.manpower = manpower;
		this.alloys = alloys;
		this.hp = hp;
		this.hardness = hardness;
		this.attack = attack;
		this.defense = defense;
		this.boardingAttack = boardingAttack;
		this.boardingDefense = boardingDefense;
		this.infantryPower = infantryPower;
		this.armorPower = armorPower;
		this.artilleryPower = artilleryPower;
		this.airPower = airPower;
		this.count = 0;
	}
	
	//Copy Constructor
	public LandUnitTemplate(long id, Faction owner, LandUnitTemplate copy)
	{
		this.id = id;
		this.owner = owner;
		this.type = copy.getType();
		this.transportType = copy.getTransportType();
		this.buildTime = copy.getBuildTime();
		this.cost = copy.getCost();
		this.manpower = copy.getManpower();
		this.alloys = copy.getAlloys();
		this.hp = copy.getMaxHP();
		this.hardness = copy.getHardness();
		this.attack = copy.getAttack();
		this.defense = copy.getDefense();
		this.boardingAttack = copy.getBoardingAttack();
		this.boardingDefense = copy.getBoardingDefense();
		this.infantryPower = copy.getInfantryPower();
		this.armorPower = copy.getArmorPower();
		this.artilleryPower = copy.getArtilleryPower();
		this.airPower = copy.getAirPower();
		this.count = 0;
	}
	
	//Next Unit Name
	public String getNextUnitName()
	{
		//If there is a stored army name, removes from storage list and returns it
		if(storedNames.size()>0)
		{
			String temp = storedNames.get(0);
			storedNames.remove(0);
			return temp;
		}
		else
		{
			String ret = "";
			
			//Increments army count, adds to string
			count++;
			ret += Integer.toString(count);
			
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
			
			ret += this.type;
			return ret;
		}
	}
	
	//Stores unit name
	public void storeName(String name) { storedNames.add(name); }
	
	//Getters
	public long getID() { return id; }
	public Faction getOwner() { return owner; }
	public String getType() { return type; }
	public String getTransportType() { return transportType; }
	public NavalUnitTemplate getTransportTemplate() { return owner.getNavalTemplate(transportType); }
	public int getBuildTime() { return buildTime; }
	public int getCost() { return cost; }
	public int getManpower() { return manpower; }
	public int getAlloys() { return alloys; }
	public int getMaxHP() { return hp; }
	public int getHardness() { return hardness; }
	public int getAttack() { return attack; }
	public int getDefense() { return defense; }
	public int getBoardingAttack() { return boardingAttack; }
	public int getBoardingDefense() { return boardingDefense; }
	public int getInfantryPower() { return infantryPower; }
	public int getArmorPower() { return armorPower; }
	public int getArtilleryPower() { return artilleryPower; }
	public int getAirPower() { return airPower; }
}
