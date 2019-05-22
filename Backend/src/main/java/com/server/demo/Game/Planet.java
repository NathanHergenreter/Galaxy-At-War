package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Arrays;


//Planet Object
//Contains coords, a list of adjacencies, and an owner player
//Contains game-logic calculation related fields 
public class Planet {
	
	private long id;
	private double[] coords = new double[2];
	private String name;
	private String climate;	//Planet's Climate name
	private int size;	//From 0-4, 4 being the largest
	private int roughness;	//From 0-100, 0 being no malus, 100 a large malus
	private double population;	//In billions, from 0.1-100
	private int mineralReserves;	//From 0-9, 9 being the richest
	@JsonBackReference
	private ArrayList<Hyperlane> hyperlanes = new ArrayList<Hyperlane>();
	
	@JsonManagedReference
	private Faction owner;
	@JsonBackReference
	private ArrayList<Army> armies = new ArrayList<Army>();
	@JsonBackReference
	private ArrayList<Fleet> fleets = new ArrayList<Fleet>();
	
	@JsonBackReference
	private ArrayList<LandUnitTemplate> unitQueue = new ArrayList<LandUnitTemplate>();
	private int unitDays;
	@JsonBackReference
	private ArrayList<NavalUnitTemplate> shipQueue = new ArrayList<NavalUnitTemplate>();
	private int shipDays;
	
	@JsonBackReference
	private LandCombat landCombat;
	@JsonBackReference
	private NavalCombat navalCombat;
	
	//Constructor
	//MapGenerator randomly generates the planet's statistics and sends them as input
	//Climate is the index in the Game's climates list
	public Planet(long id, double x, double y, String name, Climate climate, int size, int roughness,
				double population, int mineralReserves)
	{
		this.id = id;
		coords[0] = x;
		coords[1] = y;
		this.name = name;
		this.climate = climate.getName();
		this.size = size;
		this.roughness = roughness;
		this.population = population;
		this.mineralReserves = mineralReserves;
		this.owner = null;
		this.landCombat = null;
		this.navalCombat = null;
	}
	
	//Builds Land Unit
	public synchronized void buildLandUnit(LandUnitTemplate temp)
	{
		unitQueue.add(temp);
		if(unitQueue.size() == 1) { this.unitDays = temp.getBuildTime(); }
		this.owner.modResAll(-temp.getCost(), -temp.getManpower(), -temp.getAlloys());

		//Faction id, Planet id, Unit Type id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(temp.getID()))));
		game().outputMessage(new UpdateMessage("queueUnit", ids));
	}
	
	//Complete Land Unit
	private void completeLandUnit()
	{
		produceLandUnit(new LandUnit(this.unitQueue.get(0)));
		this.unitQueue.remove(0);
		if(this.unitQueue.size() > 0) { this.unitDays = this.unitQueue.get(0).getBuildTime(); }
	}
	
	//Produces Land Unit
	public synchronized void produceLandUnit(LandUnit unit)
	{
		ArrayList<LandUnit> units = new ArrayList<LandUnit>();
		units.add(unit);
		
		Army army = new Army(this.owner, units, this);
		this.addArmy(army);
		this.owner.addArmy(army);
		this.owner.addFleet(army.getTransport());
		
		//Faction id, Planet id, Army id, Unit id, Unit Type id, Transport id, Transport Type id || Army name, Transport name
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(army.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(unit.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(unit.getTemplate().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(unit.getTransport().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(unit.getTransport().getTemplate().getID()))));
		game().outputMessage(new UpdateMessage("createUnit", ids,
				new ArrayList<String>(Arrays.asList(army.getName(), army.getTransport().getName()))
				));
	}
	
	//Cancels Land Unit
	public synchronized void cancelLandUnit()
	{
		this.unitDays = 0;
		
		if(this.unitQueue.size() > 0) 
		{ 
			LandUnitTemplate temp = unitQueue.get(0);
			this.owner.modResAll(temp.getCost(), temp.getManpower(), temp.getAlloys());
			this.unitQueue.remove(0);

			//Faction id, Planet id, Unit Type id
			ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(temp.getID()))));
			game().outputMessage(new UpdateMessage("cancelUnit", ids));
			
			if(this.unitQueue.size() > 0) { this.unitDays = this.unitQueue.get(0).getBuildTime(); }
		}
	}

	//Builds Naval Unit
	public synchronized void buildNavalUnit(NavalUnitTemplate temp)
	{
		this.shipQueue.add(temp);
		if(shipQueue.size() == 1) { this.shipDays = temp.getBuildTime(); }
		this.owner.modResAll(-temp.getCost(), -temp.getManpower(), -temp.getAlloys());

		//Faction id, Planet id, Ship Type id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(temp.getID()))));
		game().outputMessage(new UpdateMessage("queueShip", ids));
	}

	//Completes Naval Unit
	private void completeNavalUnit()
	{
		produceNavalUnit(new NavalUnit(this.shipQueue.get(0)));
		this.shipQueue.remove(0);
		if(this.shipQueue.size() > 0) { this.shipDays = this.shipQueue.get(0).getBuildTime(); }
	}
	
	//Produces a Naval unit
	public synchronized void produceNavalUnit(NavalUnit ship)
	{
		ArrayList<NavalUnit> ships = new ArrayList<NavalUnit>();
		ships.add(ship);

		Fleet fleet = new Fleet(this.owner, ships, this);
		ship.setFleet(fleet);
		ship.getCargo().setArmy(fleet.getCargo());
		this.addFleet(fleet);
		this.owner.addFleet(fleet);
		this.owner.addArmy(fleet.getCargo());
		
		//Faction id, Planet id, Army id, Unit id, Unit Type id || Fleet name, Cargo name
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(fleet.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ship.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ship.getTemplate().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ship.getCargo().getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ship.getCargo().getTemplate().getID()))));
		game().outputMessage(new UpdateMessage("createShip", ids,
				new ArrayList<String>(Arrays.asList(fleet.getName(), fleet.getCargo().getName()))
						));
	}

	//Cancels Naval Unit
	public synchronized void cancelNavalUnit()
	{
		this.shipDays = 0;
		
		if(this.shipQueue.size() > 0) 
		{ 
			NavalUnitTemplate temp = shipQueue.get(0);
			this.owner.modResAll(temp.getCost(), temp.getManpower(), temp.getAlloys());
			this.shipQueue.remove(0);

			//Faction id, Planet id, Ship Type id
			ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(owner.getID()))));
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(this.id))));
			ids.add(new ArrayList<Long>(Arrays.asList(new Long(temp.getID()))));
			game().outputMessage(new UpdateMessage("cancelShip", ids));
			
			if(this.shipQueue.size() > 0) { this.shipDays = this.shipQueue.get(0).getBuildTime(); }
		}
	}
	
	//Update method, gives resources to owner and decrements/completes queue'd builds
	public synchronized void update()
	{
		if(owner != null && !owner.getPlayer().equals("Neutral Systems"))
		{
			int money = (int) this.population*100;
			money = money > 100 ? money : 100;
			int manpower = (int) this.population > 0 ? (int) this.population : 1;
			int alloys = this.mineralReserves;
			
			this.owner.modResAll(money, manpower, alloys);
		}
		
		if(this.unitQueue.size() > 0)
		{
			unitDays--;
			if(unitDays == 0) { completeLandUnit(); }
		}
		if(this.shipQueue.size() > 0)
		{
			shipDays--;
			if(shipDays == 0) { completeNavalUnit(); }
		}
		if(this.landCombat!=null)
		{
			this.landCombat.update();
			if(landCombat.complete())
			{	
				this.landCombat = null;
				if(!armies.get(0).getOwner().equals(this.owner))
				{
					this.setOwner(armies.get(0).getOwner());
				}

				//Planet id
				ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
				ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
				game().outputMessage(new UpdateMessage("combatEndLand", ids));
			}
		}
		if(this.navalCombat!=null)
		{
			this.navalCombat.update();
			if(navalCombat.complete())
			{
				this.navalCombat = null;
				
				//Planet id
				ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
				ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
				game().outputMessage(new UpdateMessage("combatEndNavalPlanet", ids));
			}
		}
	}
	
	/*
	 * Getters and Setters
	 */

	//ID
	public long getID() { return id; }
	public Game game() { return owner.game(); }
	
	//Name
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	//Coords
	public double getX() { return coords[0]; }
	public double getY() { return coords[1]; }
	
	//Adjacencies
	public ArrayList<Hyperlane> getHyperlanes() { return hyperlanes; }
	public void addHyperlane(Hyperlane lane) { hyperlanes.add(lane); }
	
	//Owner
	public Faction getOwner() { return owner; }
	
	//Removes planet from old owner's (if there was) list of planets, adds to new owner's list
	public synchronized void setOwner(Faction owner) 
	{  
		while(unitQueue.size() > 0) { cancelLandUnit(); }
		while(shipQueue.size() > 0) { cancelNavalUnit(); }
		
		if(this.owner!=null) { this.owner.removePlanet(this); }
		this.owner = owner; 
		owner.addPlanet(this); 
	}
	
	//Climate Name
	public String getClimate() { return climate; }
	
	//Size
	public int getSize() { return size; }
	
	//Roughness
	public int getRoughness() { return roughness; }
	
	//Population
	public double getPopulation() { return population; }
	
	//Mineral Reserves
	public int getMineralReserves() { return mineralReserves; }
	
	//Tax
	public double getTax() { return population; }
	
	//Manpower Output
	//Returns values in 10,000's
	public int getManpower() { return (int) population*100; }
	
	//Alloys
	public double getAlloys() { return (mineralReserves+1)*2; }
	
	//Alloy Production
	public double getAlloyProduction() { return mineralReserves+1; }
	
	//Armies
	public ArrayList<Army> getArmies() { return armies; }
	public Army getArmy(int idx) { return armies.get(idx); }
	public void addArmy(Army army) { armies.add(army); }
	public void removeArmy(Army army) { armies.remove(army); }
	
	//Fleets
	public ArrayList<Fleet> getFleets() { return fleets; }
	public Fleet getFleet(int idx) { return fleets.get(idx); }
	public void addFleet(Fleet fleet) { fleets.add(fleet); }
	public void removeFleet(Fleet fleet) { fleets.remove(fleet); }
	
	//Build Queues
	public ArrayList<LandUnitTemplate> getUnitQueue() { return unitQueue; }
	public LandUnitTemplate getQueuedUnit() { return unitQueue.get(0); }
	public int getUnitDays() { return unitDays; }
	public boolean hasQueuedUnit() { return unitQueue.size() > 0; }
	public ArrayList<NavalUnitTemplate> getShipQueue() { return shipQueue; }
	public NavalUnitTemplate getQueuedShip() { return shipQueue.get(0); }
	public int getShipDays() { return shipDays; }
	public boolean hasQueuedShip() { return shipQueue.size() > 0; }
	
	//Land Combat
	public LandCombat getLandCombat() { return landCombat; }
	public boolean hasLandCombat() { return landCombat!=null; }
	public void beginLandCombat(Army attacker) 
	{ 
		landCombat = new LandCombat(this, attacker); 

		//Planet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();;
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("combatStartLand", ids));
	}
	
	//Naval Combat
	public NavalCombat getNavalCombat() { return navalCombat; }
	public boolean hasNavalCombat() { return navalCombat!=null; }
	public void beginNavalCombat(Fleet attacker) 
	{ 
		navalCombat = new NavalCombat(this, attacker);

		//Planet id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();;
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(id))));
		game().outputMessage(new UpdateMessage("combatStartNavalPlanet", ids));
	}
}