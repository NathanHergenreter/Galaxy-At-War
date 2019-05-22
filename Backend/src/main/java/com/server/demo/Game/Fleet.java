package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Arrays;

//High Level naval unit grouping
//Contains one or more ships
//Also contains a reference to a mirror army
public class Fleet {

	private long id;
	@JsonManagedReference
	private Faction owner;
	private String name;
	@JsonBackReference
	private ArrayList<NavalUnit> ships = new ArrayList<NavalUnit>();
	@JsonManagedReference
	private Army cargo;
	@JsonManagedReference
	private Planet station;
	@JsonManagedReference
	private Hyperlane lane;
	private boolean isFleet;	//Flag for if fleet is primary (ie not transport fleet)
	
	//Moves
	@JsonManagedReference
	private ArrayList<Planet> moveQueue;
	private boolean inTransit;
	private int transitDays;
	
	//Constructor
	public Fleet(Faction owner, ArrayList<NavalUnit> ships, Planet station)
	{
		this.id = GameHelper.getAndIncFleetID();
		this.owner = owner;
		this.name = owner.getNextFleetName();
		this.ships = ships;
		this.station = station;
		this.lane = null;
		this.isFleet = true;
		this.moveQueue = new ArrayList<Planet>();
		this.inTransit = false;
		this.transitDays = 0;

		//Constructs an accompanying cargo army
		ArrayList<LandUnit> units = new ArrayList<LandUnit>();
		for(NavalUnit ship : ships)
		{
			ship.setFleet(this);
			units.add(ship.getCargo());
		}
		
		this.cargo = new Army(owner, units, this);

		for(LandUnit unit : units) { unit.setArmy(this.cargo); }
	}

	//Constructor - Lane
	public Fleet(Faction owner, ArrayList<NavalUnit> ships, Hyperlane lane)
	{
		this(owner, ships, (Planet) null);
		
		this.lane = lane;
	}

	//Constructor - Grounded fleet for newly created army
	public Fleet(Faction owner, ArrayList<NavalUnit> ships, Army cargo)
	{
		this.id = GameHelper.getAndIncFleetID();
		this.owner = owner;
		this.name = cargo.getName() + " Transport Fleet";
		this.ships = ships;
		this.station = null;
		this.lane=null;
		this.cargo = cargo;
		this.isFleet = false;
		this.moveQueue = new ArrayList<Planet>();
		this.inTransit = false;
		this.transitDays = 0;
	}
	

	//Disembarks Army
	//Fleet swaps with accompanying army, leaves orbit and lands on planet
	//Sets fleet station to null, removes fleet from planet's fleet list
	//Sets cargo army station to former fleet station, adds army to planet's army list
	//Disembarking on an enemy planet will (1) engage in combat and (2) change ownership depending on combat
	public synchronized void disembark()
	{
		Planet temp = this.station;
		temp.removeFleet(this);
		this.cargo.setStation(temp);
		this.station = null;
		
		//Checks if planet is owned by fleet owner
		//If not, engages in combat
		if(temp.getOwner()!=this.owner)
		{
			//Checks if any garrison, if not, switches ownership immediately
			if(temp.getArmies().size()==0) { temp.setOwner(this.owner); }
			
			//Otherwise is a garrison
			//Makes sure no combat is already occurring before beginning a new combat
			else if(temp.hasLandCombat()) { temp.getLandCombat().addArmy(this.cargo); }
			else { temp.beginLandCombat(this.cargo); }
		}

		temp.addArmy(this.cargo);

		//Faction id, Fleet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("fleetDisembark", ids));
	}

	//Merges this Fleet with given Fleet
	public synchronized void merge(Fleet fleet)
	{
		//Checks if both are of same primary type
		//If not, no merge is made
		if(this.isFleet==fleet.isFleet)
			{
			//Adds all ships and their cargo to the fleet and cargo army
			while(fleet.getShips().size()>0)
			{
				NavalUnit ship = fleet.getShips().get(0);
				LandUnit unit = ship.getCargo();
				fleet.removeShip(ship);
				fleet.getCargo().removeUnit(unit);
				this.addShip(ship);
				this.cargo.addUnit(unit);
			}
			
			fleet.cargo.getUnits().clear();
			fleet.disband();
		}

		//Faction id, Fleet0 id, Fleet1 id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getID()))));
		game().outputMessage(new UpdateMessage("fleetMerge", ids));
	}
	
	//Splits given list of ships into new fleet
	public synchronized void split(ArrayList<NavalUnit> ships)
	{
		//Removes listed ships from fleet
		for(NavalUnit ship : ships)
		{
			this.ships.remove(ship);
			this.cargo.removeUnit(ship.getCargo());
		}
		Fleet split;
		
		//Makes a new fleet with given ships list
		//If orbiting a planet, uses constructor with Planet
		if(this.station!=null)
		{
			split = new Fleet(this.owner, ships, this.station);
			this.owner.addFleet(split);
			this.owner.addArmy(split.cargo);
			this.station.addFleet(split);
			
		}
		//Otherwise uses Hyperlane constructor
		else
		{
			split = new Fleet(this.owner, ships, this.lane);
			this.owner.addFleet(split);
			this.owner.addArmy(split.cargo);
			this.lane.addFleet(split);
		}

		//Faction id, FleetOld id, FleetNew id, CargoNew id, Split ships ids || FleetNew name, CargoNew name
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(split.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(split.getCargo().getID()))));
		ArrayList<Long> splitList = new ArrayList<>();
		for(NavalUnit ship : ships) { splitList.add(new Long(ship.getID())); }
		ids.add(splitList);
		game().outputMessage(new UpdateMessage("fleetSplit", ids,
				new ArrayList<String>(Arrays.asList(split.getName(), split.getCargo().getName()))
				));
	}
	
	//Disbands Fleet
	//Any ships remaining in fleet will be lost
	public synchronized void disband()
	{
		//Faction id, Fleet id, Cargo id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(cargo.getID()))));
		game().outputMessage(new UpdateMessage("fleetDisband", ids));
		
		if(isFleet) { this.owner.storeFleetName(this.name); }	//Only stores name if not transport fleet
		
		//Disbands all cargo units
		for(LandUnit unit : this.cargo.getUnits())
		{
			unit.disband();
		}
		this.cargo.getUnits().clear();
		this.owner.removeArmy(this.cargo);
		this.cargo = null;
		
		//Disbands all ships
		for(NavalUnit ship : this.ships)
		{
			ship.disband();
		}
		this.ships.clear();
		this.owner.removeFleet(this);
		
		//If orbiting a planet, removes from planet's fleet list, otherwise does so for lane
		if(this.station!=null) { this.station.removeFleet(this); this.station = null; }
		else if(this.lane!=null) { this.lane.removeFleet(this); this.lane = null; }
	}
	
	//Moves fleet to planet
	public synchronized void move(Planet planet)
	{
		if(this.station!=null) { this.station.removeFleet(this); }
		else { this.lane.removeFleet(this); this.lane = null; }
		
		setStation(planet);
		planet.addFleet(this);

		//Faction id, Planet id, Fleet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(planet.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("fleetMovePlanet", ids));
	}
	
	//Moves fleet to hyperlane
	public synchronized void move(Hyperlane lane)
	{
		if(station!=null) { this.station.removeFleet(this); this.station = null; }
		else { this.lane.removeFleet(this); }
		
		setLane(lane);
		
		lane.addFleet(this);
		
		//Faction id, PtA id, PtB id, Fleet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(lane.getA().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(lane.getB().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("fleetMoveLane", ids));
	}
	
	//Resets moveQueue
	public synchronized void moveTo(Planet target)
	{
		this.cancelMove();
		
		this.moveQueue.add(target);
	}

	//Queues a Move towards target
	public synchronized void queueMove(Planet target) 
	{ 
		this.moveQueue.add(target); 

		//Faction id, Planet id, Fleet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(target.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("fleetMoveQueue", ids));
	}
	
	//Cancels a move, returns ship to previous planet
	public synchronized void cancelMove()
	{
		if(this.moveQueue.size()>0)
		{
			Planet target = this.moveQueue.get(0);
			
			if(this.inTransit)
			{
				Planet origin = this.lane.getA().equals(target) ? this.lane.getB() : this.lane.getA();
				this.move(origin);
				this.inTransit = false;
			}
			
			this.moveQueue.clear();
		}

		//Faction id, Fleet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		game().outputMessage(new UpdateMessage("fleetMoveCancel", ids));
	}
	
	//Updates Fleet
	public synchronized void update()
	{
		//If not in naval combat, updates
		if((this.station != null && !this.station.hasNavalCombat())
				|| (this.lane != null && !this.lane.hasNavalCombat()))
		{
			//Heals ship by 5% and craft by 20%
			for(NavalUnit ship : this.getShips())
			{
				if(ship.getHP() != ship.getMaxHP())
				{
					int hpRegen = (ship.getMaxHP()*5)/100;
					int hpShip = ship.getHP() + hpRegen < ship.getMaxHP() ? ship.getHP() + hpRegen : ship.getMaxHP();
					ship.setHP(hpShip);
				}

				if(ship.getCraftHP() != ship.getMaxCraftHP())
				{
					int hpCraftRegen = (ship.getMaxCraftHP()*20)/100;
					int hpCraft = ship.getCraftHP() + hpCraftRegen < ship.getMaxCraftHP() ? ship.getCraftHP() + hpCraftRegen : ship.getMaxCraftHP();
					ship.setCraftHP(hpCraft);
				}
			}
			
			//Moves and Ship HP Regen
			if(this.moveQueue.size() > 0)
			{
				Planet target = this.moveQueue.get(0);
				
				//Moves fleet into transit if not already so
				if(!inTransit)
				{
					this.inTransit = true;
					
					//Looks for target planet on end of lane. Said lane is the lane that is moved to
					for(Hyperlane lane : this.getStation().getHyperlanes())
					{
						Planet opposite = this.getStation().equals(lane.getA()) ? lane.getB() : lane.getA();
						
						//Moves to transit lane
						if(opposite.equals(target)) 
						{
							this.transitDays = (int) lane.getDistance() + 1;
							if(lane.hasNavalCombat())
							{
								lane.getNavalCombat().addFleet(this);
							}
							else if(lane.getFleets().size() > 0 && !lane.getFleet(0).getOwner().equals(this.owner))
							{
								lane.beginNavalCombat(this);
							}
							this.move(lane);
							break;
						}
					}
				}
				
				transitDays--;
				
				//Checks if move is complete, if so moves and removes from queue
				if(this.transitDays==0) 
				{
					if(target.hasNavalCombat())
					{
						target.getNavalCombat().addFleet(this);
					}
					else if(target.getFleets().size() > 0 && !target.getFleet(0).getOwner().equals(this.owner))
					{
						target.beginNavalCombat(this);
					}
					this.move(target);
					this.moveQueue.remove(0);
					this.inTransit = false;
				}
			}
			
			//Cargo HP Regen by 5% if not in combat
			if(this.cargo.getStation() != null && !this.cargo.getStation().hasLandCombat())
			{
				for(LandUnit unit : this.cargo.getUnits())
				{
					if(unit.getHP() != unit.getMaxHP())
					{
						int hpRegen = (unit.getMaxHP()*5)/100;
						int hpUnit = unit.getHP() + hpRegen < unit.getMaxHP() ? unit.getHP() + hpRegen : unit.getMaxHP();
						unit.setHP(hpUnit);
					}
				}
			}
		}
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
		if(isFleet) { this.owner.storeFleetName(this.name); }	//Only stores name if not transport fleet
		this.name = name;
	}
	
	//Ships
	public ArrayList<NavalUnit> getShips() { return ships; }
	public NavalUnit getShip(int idx) { return ships.get(idx); }
	public void addShip(NavalUnit ship)
	{
		ships.add(ship); 
		ship.setFleet(this);
		
		//If ship's cargo is not in cargo army, adds it
		if(!cargo.getUnits().contains(ship.getCargo()))
		{
			cargo.addUnit(ship.getCargo()); 
			ship.getCargo().setArmy(cargo);
		}
	}
	public void removeShip(NavalUnit ship)
	{ 
		ships.remove(ship); 
		ship.setFleet(null);
		
		//If ship's cargo is in cargo army, removes it
		if(cargo.getUnits().contains(ship.getCargo()))
		{
			cargo.removeUnit(ship.getCargo()); 
		}
	}
	public void removeShip(long id)
	{
		for(NavalUnit ship : ships)
		{
			if(ship.getID() == id) { removeShip(ship); break; }
		}
	}
	
	//Cargo Army - Note, cargo should only be modified by adding/removing ships
	public Army getCargo() { return cargo; }
	
	//Station
	public Planet getStation() { return station; }
	public void setStation(Planet station) { this.station = station; }
	
	//Hyperlane
	public Hyperlane getLane() { return lane; }
	public void setLane(Hyperlane lane) { this.lane = lane; }
	
	//Fleet is Primary
	public boolean isFleet() { return isFleet; }
	
	//Moves
	public Planet getMove() { return moveQueue.get(0); }
	public ArrayList<Planet> getMoveQueue() { return moveQueue; }
	public boolean inTransit() { return inTransit; }
	public int remTransitTime() { return transitDays; }
}
