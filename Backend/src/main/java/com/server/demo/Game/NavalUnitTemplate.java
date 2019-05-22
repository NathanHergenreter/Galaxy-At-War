package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.sql.Template;

//Template for a naval unit
//Contains its type name and all of its stats
//Each instance of NavalUnit refers back to this template to get stat info
public class NavalUnitTemplate {

	private long id;
	@JsonManagedReference
	private Faction owner;
	private String type;
	private String cargoType;	//Land Unit type created alongside this unit ("none" if no land attachment)
	private int buildTime;	//Number of days to build unit
	private int cost;	//Cost in money
	private int manpower;	//Cost in manpower
	private int alloys;	//Cost in alloys
	private int hp;	//Maximum HP
	private int size;	//Size of ship, higher values make ship easier to hit
	private int armor;	//Reduces damage taken
	private int evasion;	//Increases chance of avoiding attacks (reduces damage)
	private int firepower;	//Damage output of ship
	private int range;	//Determines what phase ship starts firing
	private int craftHP;	//Max HP of onboard spacecrafts
	private int craftPower;	//Damage output of onboard crafts
	private int craftDefense;	//Damage output towards crafts attacking this unit
	private ArrayList<String> names = new ArrayList<String>();
	
	//Constructor - takes in all variable data
	public NavalUnitTemplate(Faction owner, String type, String cargoType,
							int buildTime, int cost, int manpower, int alloys,
							int hp, int size, int armor, int evasion,
							int firepower, int range, int craftHP, int craftPower, int craftDefense,
							ArrayList<String> names)
	{
		this.id = -1;
		this.owner = owner;
		this.type = type;
		this.cargoType = cargoType;
		this.buildTime = buildTime;
		this.cost = cost;
		this.manpower = manpower;
		this.alloys = alloys;
		this.hp = hp;
		this.size = size;
		this.armor = armor;
		this.evasion = evasion;
		this.firepower = firepower;
		this.range = range;
		this.craftHP = craftHP;
		this.craftPower = craftPower;
		this.craftDefense = craftDefense;
		this.names = names;
	}

	//Copy Constructor
	public NavalUnitTemplate(long id, Faction owner, NavalUnitTemplate copy)
	{
		this.id = id;
		this.owner = owner;
		this.type = copy.getType();
		this.cargoType = copy.getCargoType();
		this.buildTime = copy.getBuildTime();
		this.cost = copy.getCost();
		this.manpower = copy.getManpower();
		this.alloys = copy.getAlloys();
		this.hp = copy.getMaxHP();
		this.size = copy.getSize();
		this.armor = copy.getArmor();
		this.evasion = copy.getEvasion();
		this.firepower = copy.getFirepower();
		this.range = copy.getRange();
		this.craftHP = copy.getMaxCraftHP();
		this.craftPower = copy.getCraftPower();
		this.craftDefense = copy.getCraftDefense();
		this.names = copy.names;
	}
	
	//Next Ship Name
	public String getNextShipName()
	{
		//If there are names left in the list, returns a randomly chosen name
		if(names.size()>0)
		{
			Random rng = new Random();
			int idx = rng.nextInt(names.size());
			String temp = names.get(idx);
			names.remove(idx);
			return temp;
		}
		//Otherwise, name is ship type
		else
		{
			return type;
		}
	}

	//Stores unit name
	public void storeName(String name) { names.add(name); }
	
	//Getters
	public long getID() { return id; }
	public Faction getOwner() { return owner; }
	public String getType() { return type; }
	public String getCargoType() { return cargoType; }
	public LandUnitTemplate getCargoTemplate() { return owner.getLandTemplate(cargoType); }
	public int getBuildTime() { return buildTime; }
	public int getCost() { return cost; }
	public int getManpower() { return manpower; }
	public int getAlloys() { return alloys; }
	public int getMaxHP() { return hp; }
	public int getSize() { return size; }
	public int getArmor() { return armor; }
	public int getEvasion() { return evasion; }
	public int getFirepower() { return firepower; }
	public int getRange() { return range; }
	public int getMaxCraftHP() { return craftHP; }
	public int getCraftPower() { return craftPower; }
	public int getCraftDefense() { return craftDefense; }
}
