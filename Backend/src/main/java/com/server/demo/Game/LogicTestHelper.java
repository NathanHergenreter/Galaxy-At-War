package com.server.demo.Game;

import java.util.ArrayList;

public class LogicTestHelper {
	GameManagerTest test;
	Game game;
	
	public LogicTestHelper(GameManagerTest test, Game game)
	{
		this.test = test;
		this.game = game;
	}
	

	//Outputs planet info
	public void outputPlanet(Planet c)
	{
    	System.out.println(c.getName());
    	System.out.println("ID: "+c.getID());
    	System.out.print("Owner: ");
    	if(c.getOwner()!=null) { System.out.println(c.getOwner().getPlayer()); }
    	else { System.out.println("None"); }
    	System.out.print("Owner ID: ");
    	if(c.getOwner()!=null) { System.out.println(c.getOwner().getID()); }
    	else { System.out.println("NA"); }
    	System.out.println("Size: "+c.getSize());
    	System.out.println("Climate: "+c.getClimate());
    	System.out.println("Population: "+c.getPopulation() + " billion");
    	System.out.println("Roughness: "+c.getRoughness()+"%");
    	System.out.println("Mineral Reserves: "+c.getMineralReserves());
    	System.out.print("Constructing Unit: ");
    	if(c.hasQueuedUnit()) { System.out.println(c.getQueuedUnit().getType()+" in "+c.getUnitDays()+" days"); }
    	else { System.out.println("None"); }
    	System.out.print("Constructing Ship: ");
    	if(c.hasQueuedShip()) { System.out.println(c.getQueuedShip().getType()+" in "+c.getShipDays()+" days"); }
    	else { System.out.println("None"); }
    	
    	for(Hyperlane h : c.getHyperlanes())
    	{
    		if(h.getA()==c)
    		{
    			System.out.println("Lane of distance "+h.getDistance()+" to "+h.getB().getName());
    		}
    		else
    		{
    			System.out.println("Lane of distance "+h.getDistance()+" to "+h.getA().getName());
    		}
    	}
    	System.out.println();
	}
	
	//Outputs map as 2D array of chars
	public void outputMap()
	{
	    ArrayList<Planet> planets = game.getPlanets();
	    
	    int d = 80;
	    char[][] map = new char[d][];
	    for(int i = 0; i<d; i++) { map[i] = new char[d]; }
	    for(int i = 0; i<d; i++) { for(int j = 0; j<d; j++) { map[i][j]='.'; } }
	    for(Planet c : planets) { map[(int) c.getX()+d/2][(int) c.getY()+d/2] = c.getOwner().getPlayer().charAt(0); }
	    for(int i = 0; i<d; i++) { 
	    	for(int j = 0; j<d; j++) { System.out.print(map[i][j]); }
	    	System.out.println();
	    	}
		System.out.println();
	}
	
	//Outputs land units templates and their info
	public void outputLandUnit(LandUnitTemplate temp)
	{
		System.out.println(temp.getType());
		System.out.println("Transport Type: "+temp.getTransportType());
		System.out.println("Build Time: "+temp.getBuildTime());
		System.out.println("Cost: "+temp.getCost());
		System.out.println("Manpower Cost: "+temp.getManpower());
		System.out.println("Alloys Cost: "+temp.getAlloys());
		System.out.println("Max HP: "+temp.getMaxHP());
		System.out.println("Hardness: "+temp.getHardness());
		System.out.println("Attack: "+temp.getAttack());
		System.out.println("Defense: "+temp.getDefense());
		System.out.println("Boarding Attack: "+temp.getBoardingAttack());
		System.out.println("Boarding Defense: "+temp.getBoardingDefense());
		System.out.println("Infantry Power: "+temp.getInfantryPower());
		System.out.println("Armor Power: "+temp.getArmorPower());
		System.out.println("Artillery Power: "+temp.getArtilleryPower());
		System.out.println("Air Support Power: "+temp.getAirPower());
		System.out.println();
	}

	//Outputs naval units templates and their info
	public void outputNavalUnit(NavalUnitTemplate temp)
	{
		System.out.println(temp.getType());
		System.out.println("Cargo Type: "+temp.getCargoType());
		System.out.println("Build Time: "+temp.getBuildTime());
		System.out.println("Cost: "+temp.getCost());
		System.out.println("Manpower Cost: "+temp.getManpower());
		System.out.println("Alloys Cost: "+temp.getAlloys());
		System.out.println("Max HP: "+temp.getMaxHP());
		System.out.println("Size: "+temp.getSize());
		System.out.println("Armor: "+temp.getArmor());
		System.out.println("Evasion: "+temp.getEvasion());
		System.out.println("Firepower: "+temp.getFirepower());
		System.out.println("Range: "+temp.getRange());
		System.out.println("Max Support Craft HP: "+temp.getMaxCraftHP());
		System.out.println("Support Craft Power: "+temp.getCraftPower());
		System.out.println("Support Craft Defense: "+temp.getCraftDefense());
		System.out.println();
	}
	
	//Outputs military
	public void outputMilitary(Faction faction, String message)
	{
	    System.out.println();
	    System.out.println(message);
	    System.out.print("Armies: ");
	    for(Army army : faction.getArmies()) { if(army.isArmy()) System.out.print(army.getName()+", "); }
	    System.out.println();
	    System.out.print("Fleets: ");
	    for(Fleet fleet : faction.getFleets()) { if(fleet.isFleet()) System.out.print(fleet.getName()+", "); }
	    System.out.println();
	    System.out.println("Number of armies: "+faction.getArmies().size());
	    System.out.println("Number of fleets: "+faction.getFleets().size());
	    System.out.println();
	}
	
	//Outputs Army
	public void outputArmy(Army army)
	{
		System.out.println();
		System.out.println(army.getName());
		System.out.println("ID: "+army.getID());
		System.out.println("Owner: "+army.getOwner().getPlayer());
		if(army.getStation()!=null) { System.out.println("Location: "+army.getStation().getName()); }
		else { System.out.println("Location: In orbit"); }
		System.out.println("Number of Units: "+army.getUnits().size());
		System.out.println("Transport Fleet: "+army.getTransport().getName());
	}

	//Outputs Army units
	public void outputArmyUnits(Army army)
	{
		System.out.println("--Units--");
		ArrayList<LandUnit> units = army.getUnits();
		for(LandUnit unit : units)
		{
			System.out.println("   "+"ID: "+unit.getID());
			System.out.println("   "+unit.getType());
			System.out.println("   "+"Type ID: "+unit.getTemplate().getID());
			System.out.println("   "+"HP: "+unit.getHP()+"/"+unit.getMaxHP());
			System.out.println();
		}
	}
	
	//Outputs Fleet
	public void outputFleet(Fleet fleet)
	{
		System.out.println();
		System.out.println(fleet.getName());
		System.out.println("ID: "+fleet.getID());
		System.out.println("Owner: "+fleet.getOwner().getPlayer());
		if(fleet.getStation()!=null) { System.out.println("Orbiting: "+fleet.getStation().getName()); }
		else if(fleet.getLane()!=null) { 
			System.out.println("On Hyperlane between "+fleet.getLane().getA().getName()
					+" and "+fleet.getLane().getB().getName()); }
		else { System.out.println("Location: Disembarked"); }
		if(fleet.getMoveQueue().size()>0)
		{
			System.out.println("Moving to "+fleet.getMoveQueue().get(0).getName());
			if(fleet.inTransit())
				System.out.println("Will exit hyperlane travel in "+fleet.remTransitTime()+" days");
			else
				System.out.println("Entering hyperlane travel");
		}
		System.out.println("Number of Ships: "+fleet.getShips().size());
		System.out.println("Cargo Army: "+fleet.getCargo().getName());
	}
	
	//Outputs Fleet ships
	public void outputFleetShips(Fleet fleet)
	{
		System.out.println("--Ships--");
		ArrayList<NavalUnit> ships = fleet.getShips();
		for(NavalUnit ship : ships)
		{
			System.out.println("   "+"ID: "+ship.getID());
			System.out.println("   "+ship.getType());
			System.out.println("   "+"Type ID: "+ship.getTemplate().getID());
			System.out.println("   "+"HP: "+ship.getHP()+"/"+ship.getMaxHP());
			System.out.println("   "+"Craft HP: "+ship.getCraftHP()+"/"+ship.getMaxCraftHP());
			System.out.println("   "+"Marine HP: "+ship.getCargo().getHP()+"/"+ship.getCargo().getMaxHP());
			System.out.println();
		}
	}
	
	//Lists planets
	public void outputPlanetList(ArrayList<Planet> planets)
	{
		System.out.println();
		for(Planet p: planets)
		{
			System.out.println(p.getID()+" "+p.getName());
		}
	}
	//Lists factions
	public void outputFactionList()
	{
		System.out.println();
		for(Faction f: game.getFactions())
		{
			System.out.println(f.getID()+" "+f.getPlayer());
		}
	}
	//Lists given Armies
	public void outputArmyList(ArrayList<Army> armies)
	{
		System.out.println();
		for(Army a: armies)
		{
			if(a.getStation()!=null)
				System.out.println(a.getID()+" "+a.getName());
		}
	}
	//Lists given Fleets
	public void outputFleetList(ArrayList<Fleet> fleets)
	{
		System.out.println();
		for(Fleet f: fleets)
		{
			if(f.getStation()!=null || f.getLane()!=null)
				System.out.println(f.getID()+" "+f.getName());
		}
	}
	//Lists player Unit templates
	public void outputUnitTypesList(Faction faction)
	{
		ArrayList<LandUnitTemplate> temps = faction.getLandTemplates();
		System.out.println();
		for(LandUnitTemplate t : temps)
		{
			if(t.getID()%temps.size()>0 && !t.getTransportType().equals("none"))
				System.out.println(t.getID()+" "+t.getType());
		}
	}
	//Lists player Ship templates
	public void outputShipTypesList(Faction faction)
	{
		ArrayList<NavalUnitTemplate> temps = faction.getNavalTemplates();
		System.out.println();
		for(NavalUnitTemplate t : temps)
		{
			if(t.getID()%temps.size()>0 && !t.getCargoType().equals("none"))
				System.out.println(t.getID()+" "+t.getType());
		}
	}
	
	//Print outs top bar
	public void topBar()
	{
		Faction me = game.getFactions().get(1);
		System.out.println("Day "+game.getDay());
		System.out.println("$ "+me.getMoney()+" M: "+me.getManpower()+" A: "+me.getAlloys());
	}
	
	//Returns value of active armies
	public int activeArmies(ArrayList<Army> armies)
	{
		int count = 0;
		for(Army c : armies)
		{
			if(c.getStation()!=null) { count++; }
		}
		return count;
	}
	
	//Returns value of active fleets
	public int activeFleets(ArrayList<Fleet> fleets)
	{
		int count = 0;
		for(Fleet c : fleets)
		{
			if(c.getStation()!=null) { count++; }
		}
		return count;
	}
}
