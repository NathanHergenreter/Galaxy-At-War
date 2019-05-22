package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Arrays;

//High Level land unit grouping
//Contains one or more land units
//Also contains a reference to a fleet used for transport
public class Army {

	private long id;
	@JsonManagedReference
	private Faction owner;
	private String name;
	private ArrayList<LandUnit> units = new ArrayList<LandUnit>();
	@JsonManagedReference
	private Fleet transport;
	@JsonManagedReference
	private Planet station;
	private boolean isArmy;	//Flag for if the army is primary (ie not marine attachment)
	
	//Constructor - Newly created army
	public Army(Faction owner, ArrayList<LandUnit> units, Planet station)
	{
		this.id = GameHelper.getAndIncArmyID();
		this.owner = owner;
		this.name = owner.getNextArmyName();
		this.units = units;
		this.station = station;
		this.isArmy = true;
		
		//Constructs an accompanying transportation fleet
		ArrayList<NavalUnit> ships = new ArrayList<NavalUnit>();
		for(LandUnit unit : units)
		{
			unit.setArmy(this);
			ships.add(unit.getTransport());
		}
		
		this.transport = new Fleet(owner, ships, this);
		
		for(NavalUnit ship : ships) { ship.setFleet(this.transport); }
	}
	
	//Constructor - Embarked army on newly created fleet
	public Army(Faction owner, ArrayList<LandUnit> units, Fleet transport)
	{
		this.id = GameHelper.getAndIncArmyID();
		this.owner = owner;
		this.name = transport.getName() + " Marine Force";
		this.units = units;
		this.station = null;
		this.transport = transport;
		this.isArmy = false;
	}

	//Embarks Army
	//Army swaps with accompanying fleet, leaves planet and enters orbit
	//Sets army station to null, removes army from planet's army list
	//Sets transport fleet station to former army station, adds fleet to planet's fleet list
	public synchronized void embark()
	{
		Planet temp = this.station;
		temp.removeArmy(this);
		this.transport.setStation(temp);

		//Checks if any orbiting fleets are owned by enemies and no combat is already started
		//If so, begins naval combat
		if(temp.hasNavalCombat())
		{
			temp.getNavalCombat().addFleet(this.transport);
		}
		else if(temp.getFleets().size() > 0 && temp.getFleet(0).getOwner()!=this.owner)
		{
			temp.beginNavalCombat(this.transport);
		}

		temp.addFleet(this.transport);
		this.station = null;
		
		//Faction id, Army id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("armyEmbark", ids));
	}
	
	//Merges this Army with given Army
	public synchronized void merge(Army army)
	{
		//Checks if both are of same primary type
		//If not, no merge is made
		if(this.isArmy==army.isArmy)
		{
			//Adds all units and their transports to the army and transport fleet
			while(army.getUnits().size()>0)
			{
				LandUnit unit = army.getUnits().get(0);
				army.removeUnit(unit);
				this.addUnit(unit);
			}
			
			army.getTransport().getShips().clear();
			army.disband();
		}

		//Faction id, Army0 id, Army1 id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getID()))));
		game().outputMessage(new UpdateMessage("armyMerge", ids));
	}
	
	//Splits given list of units into new army
	public synchronized void split(ArrayList<LandUnit> units)
	{
		//Removes listed units from fleet
		for(LandUnit unit : units)
		{
			this.units.remove(unit);
			this.transport.removeShip(unit.getTransport());
		}
		
		//Makes a new army with given units list
		Army split = new Army(this.owner, units, this.station);
		this.owner.addArmy(split);
		this.owner.addFleet(split.transport);
		this.station.addArmy(split);

		//Faction id, ArmyOld id, ArmyNew id, TransportNew id, Split units ids || ArmyNew name, TransportNew name
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(split.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(split.getTransport().getID()))));
		ArrayList<Long> splitList = new ArrayList<>();
		for(LandUnit unit : units) { splitList.add(new Long(unit.getID())); }
		ids.add(splitList);
		game().outputMessage(new UpdateMessage("armySplit", ids,
				new ArrayList<String>(Arrays.asList(split.getName(), split.getTransport().getName()))
						));
	}
	
	//Disbands Army
	//Any units remaining in army will be lost
	public synchronized void disband()
	{
		//Faction id, Army id, Transport id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(transport.getID()))));
		game().outputMessage(new UpdateMessage("armyDisband", ids));
		
		if(isArmy) { this.owner.storeArmyName(this.name); }	//Only stores name if not marine force

		//Disbands all transports
		for(NavalUnit ship : this.transport.getShips())
		{
			ship.disband();
		}
		this.transport.getShips().clear();

		this.owner.removeFleet(this.transport);
		this.transport = null;

		//Disbands all units
		for(LandUnit unit : this.units)
		{
			unit.disband();
		}
		this.units.clear();
		this.owner.removeArmy(this);
		if(this.station!=null) { this.station.removeArmy(this); }
		this.station = null;
	}

	//Moves army to another position
	public synchronized void move(Planet planet)
	{
		embark();
		this.transport.move(planet);
		this.transport.disembark();
	}
	
	/*
	 * Getters and Setters
	 */

	//ID
	public long getID() { return id; }
	public Game game() { return owner.game(); }
	
	//Owner
	public Faction getOwner() { return owner; }
	public void removeOwner() { this.owner = null; }

	//Name
	public String getName() { return name; }
	public void setName(String name)
	{
		this.owner.storeArmyName(this.name);
		this.name = name;
	}
	
	//Units
	public ArrayList<LandUnit> getUnits() { return units; }
	public LandUnit getUnit(int idx) { return units.get(idx); }
	public void addUnit(LandUnit unit) 
	{ 
		units.add(unit); 
		unit.setArmy(this);
		
		//If unit's transport is not in transport fleet, adds it
		if(!transport.getShips().contains(unit.getTransport()))
		{
			transport.addShip(unit.getTransport()); 
			unit.getTransport().setFleet(transport);
		}
	}
	public void removeUnit(LandUnit unit) 
	{ 
		units.remove(unit); 
		unit.setArmy(null);
		
		//If unit's transport is in transport fleet, removes it
		if(transport.getShips().contains(unit.getTransport()))
		{
			transport.removeShip(unit.getTransport()); 
		}
	}
	public void removeUnit(long id)
	{
		for(LandUnit unit : units)
		{
			if(unit.getID() == id) { removeUnit(unit); break; }
		}
	}
	
	//Transport Fleet - Note, transport should only be modified by adding/removing units
	public Fleet getTransport() { return transport; }
	
	//Station
	public Planet getStation() { return station; }
	public void setStation(Planet station) { this.station = station; }
	
	//Army is Primary
	public boolean isArmy() { return isArmy; }
}
