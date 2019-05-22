package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Arrays;

//Core object for naval military
//Contains a reference to its template which contains combat stats
//Contains a reference to its corresponding cargo land unit
public class NavalUnit {
	
	private long id;
	private NavalUnitTemplate template;
	private String name;
	private Fleet fleet;
	private LandUnit cargo;
	private int hp;
	private int craftHP;
	
	//Constructor for a newly created unit
	public NavalUnit(NavalUnitTemplate template)
	{
		this.id = GameHelper.getAndIncShipID();
		this.template = template;
		this.name = template.getNextShipName();
		this.fleet = null;
		this.cargo = new LandUnit(template.getCargoTemplate(), this);
		this.hp = template.getMaxHP();
		this.craftHP = template.getMaxCraftHP();
	}
	
	//Constructor made for a unit accompanying a new ship
	public NavalUnit(NavalUnitTemplate template, LandUnit cargo)
	{
		this.id = GameHelper.getAndIncShipID();
		this.template = template;
		this.name = template.getNextShipName();
		this.fleet = null;
		this.cargo = cargo;
		this.hp = template.getMaxHP();
	}

	//Disbands unit, stores name and removes references
	public void disband()
	{
		//Faction id, Fleet id, Ship id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getOwner().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("shipDisband", ids));
		
		this.template.storeName(this.name);
		if(this.fleet != null && this.fleet.getShips().contains(this)) { this.fleet.removeShip(this); }
		this.fleet = null;
		
		if(this.cargo.getArmy() != null) { this.cargo.getArmy().removeUnit(this.cargo.getID()); }
		this.cargo = null;
	}
	
	//ID
	public long getID() { return id; }
	public Game game() { return fleet.game(); }
	
	//Template
	public NavalUnitTemplate getTemplate() { return template; }
	
	//Fleet
	public Fleet getFleet() { return fleet; }
	public void setFleet(Fleet fleet) { this.fleet = fleet; }
	
	//Transport
	public LandUnit getCargo() { return cargo; }
	public void setCargo(LandUnit cargo) { this.cargo = cargo; }
	
	//Current HP
	public int getHP() { return hp; }
	public void setHP(int hp) 
	{ 
		this.hp = hp; 

		//Faction id, Fleet id, Ship id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getOwner().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("shipHP", ids, hp));
	}
	
	//Current Craft HP
	public int getCraftHP() { return craftHP; }
	public void setCraftHP(int hp) 
	{ 
		this.craftHP = hp;

		//Faction id, Fleet id, Ship id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getOwner().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("shipCraftHP", ids, hp));
	}
	
	//Template Getters
	public String getType() { return template.getType(); }
	public int getBuildTime() { return template.getBuildTime(); }
	public int getCost() { return template.getCost(); }
	public int getManpower() { return template.getManpower(); }
	public int getAlloys() { return template.getAlloys(); }
	public int getMaxHP() { return template.getMaxHP(); }
	public int getSize() { return template.getSize(); }
	public int getArmor() { return template.getArmor(); }
	public int getEvasion() { return template.getEvasion(); }
	public int getFirepower() { return template.getFirepower(); }
	public int getRange() { return template.getRange(); }
	public int getMaxCraftHP() { return template.getMaxCraftHP(); }
	public int getCraftPower() { return template.getCraftPower(); }
	public int getCraftDefense() { return template.getCraftDefense(); }
}
