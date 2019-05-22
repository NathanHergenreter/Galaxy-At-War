package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Arrays;

//Core object for land military
//Contains a reference to its template which contains combat stats
//Contains a reference to its corresponding transport ship
public class LandUnit {

	private long id;
	private LandUnitTemplate template;
	private String name;
	private Army army;
	private NavalUnit transport;
	private int hp;
	
	//Constructor for a newly created unit
	public LandUnit(LandUnitTemplate template)
	{
		this.id = GameHelper.getAndIncUnitID();
		this.template = template;
		this.name = template.getNextUnitName();
		this.army = null;
		this.transport = new NavalUnit(template.getTransportTemplate(), this);
		this.hp = template.getMaxHP();
	}
	
	//Constructor made for a unit accompanying a new ship
	public LandUnit(LandUnitTemplate template, NavalUnit transport)
	{
		this.id = GameHelper.getAndIncUnitID();
		this.template = template;
		this.name = template.getNextUnitName();
		this.army = null;
		this.transport = transport;
		this.hp = this.template.getMaxHP();
	}
	
	//Disbands unit, stores name and removes references
	public void disband()
	{
		//Faction id, Army id, Unit id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getOwner().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("unitDisband", ids));
		
		this.template.storeName(this.name);
		if(this.army != null && this.army.getUnits().contains(this)) { this.army.removeUnit(this); }
		this.army = null;

		if(this.transport.getFleet() != null) { this.transport.getFleet().removeShip(this.transport.getID()); }
		this.transport = null;
	}

	//ID
	public long getID() { return id; }
	public Game game() { return army.game(); }
	
	//Template
	public LandUnitTemplate getTemplate() { return template; }
	
	//Army
	public Army getArmy() { return army; }
	public void setArmy(Army army) { this.army = army; }
	
	//Transport
	public NavalUnit getTransport() { return transport; }
	public void setTransport(NavalUnit transport) { this.transport = transport; }
	
	//Current HP
	public int getHP() { return hp; }
	public void setHP(int hp) 
	{ 
		this.hp = hp;

		//Faction id, Army id, Unit id || New HP value
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getOwner().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("unitHP", ids, hp));
	}
	
	//Template Getters
	public String getType() { return template.getType(); }
	public int getBuildTime() { return template.getBuildTime(); }
	public int getCost() { return template.getCost(); }
	public int getManpower() { return template.getManpower(); }
	public int getAlloys() { return template.getAlloys(); }
	public int getMaxHP() { return template.getMaxHP(); }
	public int getHardness() { return template.getHardness(); }
	public int getAttack() { return template.getAttack(); }
	public int getDefense() { return template.getDefense(); }
	public int getBoardingAttack() { return template.getBoardingAttack(); }
	public int getBoardingDefense() { return template.getBoardingDefense(); }
	public int getInfantryPower() { return template.getInfantryPower(); }
	public int getArmorPower() { return template.getArmorPower(); }
	public int getArtilleryPower() { return template.getArtilleryPower(); }
	public int getAirPower() { return template.getAirPower(); }
}
