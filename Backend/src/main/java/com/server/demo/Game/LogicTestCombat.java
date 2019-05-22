package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Scanner;

public class LogicTestCombat {

	private Game game;
	private Scanner in;
	private LogicTestHelper helper;
	private Planet planet;
	private LandCombat landCombat;
	private NavalCombat navalCombat;
	private ArrayList<LandUnitTemplate> landTemps;
	private ArrayList<NavalUnitTemplate> navalTemps;
	
	public LogicTestCombat(Game game, Scanner in, LogicTestHelper helper)
	{
		this.game = game;
		this.in = in;
		this.helper = helper;
		this.landTemps = game.getFactions().get(0).getLandTemplates();
		this.navalTemps = game.getFactions().get(0).getNavalTemplates();
	}
	
	//Land Battle Simulator
	public void goLand()
	{
		String input;
		
		//Combatants number
    	System.out.println();
		System.out.print("Enter Number of Combatants (2-n): ");
    	int numCombatants = in.nextInt();
    	System.out.println();

		//Selects planet roughness
    	System.out.println();
		System.out.print("Enter Planet Roughness (0-100): ");
    	int roughness = in.nextInt();
    	System.out.println();
    	
    	//Planet established
		this.planet = new Planet(0, 0, 0, "Battleground", game.getClimates().get(0), 0, roughness, 0, 0);

    	//Army Groups creation
    	ArrayList<ArrayList<Army>> armyGroups = new ArrayList<ArrayList<Army>>();
    	for(int i = 0; i < numCombatants; i++)
    	{
    		Faction faction = new Faction((long) i, game, "Faction "+i, "", 0);
    		armyGroups.add(new ArrayList<Army>());
    		
    		//Selects number of armies
        	System.out.println();
    		System.out.print("Enter Number of Armies for Faction "+i+" (1-n): ");
        	int numArmies = in.nextInt();
        	System.out.println();
        	
        	//Armies creation
        	for(int j = 0; j < numArmies; j++)
        	{
        		Army army = new Army(faction, new ArrayList<LandUnit>(), (Planet) null);
        		faction.addArmy(army);
        		
			    input = " ";
			    while(input.charAt(0) != 'e')
			    {
			    	System.out.println();
			    	System.out.println("Army "+j+" of "+faction.getPlayer());
			    	System.out.println("Units: "+army.getUnits().size());
					System.out.println("0: Display Units");
					System.out.println("1: Add Unit");
					System.out.println("2: Remove Unit");
					System.out.println("3: Output Unit Stats");
					System.out.println("e: Continue");
		
			    	input = in.next();
			    	
			    	switch(input.charAt(0))
			    	{
			    	//Display Units
			    	case '0':
			    		helper.outputArmyUnits(army);
			    		break;
			    		
			    	//Add Unit
			    	case '1':
			    		addUnit(army, in);
			    		break;
			    	
			    	//Remove Unit
			    	case '2':
			    		removeUnit(army, in);
			    		break;

			    	//Output Unit Templates
			    	case '3':
			    		for(LandUnitTemplate temp : landTemps) { helper.outputLandUnit(temp); }
			    		break;
		
			    	//Continue
			    	case 'e':
			    		break;
		
			    	default:
			    		System.out.println();
			    		System.out.println("Invalid Input");
			    		System.out.println();
			    		break;
			    	}
			    }
			    
			    armyGroups.get(i).add(army);
        	}
    	}
    	
    	//Places Faction i on planet, starts land combat
    	planet.setOwner(armyGroups.get(0).get(0).getOwner());
    	for(Army army : armyGroups.get(0)) { planet.addArmy(army); }
    	planet.beginLandCombat(armyGroups.get(1).get(0));
    	landCombat = planet.getLandCombat();
    	for(int i = 1; i < armyGroups.size(); i++) 
    	{ 
    		for(int j = 0; j < armyGroups.get(i).size(); j++)
    		{
    			if((i==1 && j!= 0) || (i!=1 && j==0)) { landCombat.addArmy(armyGroups.get(i).get(j));} 
    		}
    	}
    	
    	selectLand(in);
    	
    	for(ArrayList<Army> armyGroup : armyGroups) { 
    		if(armyGroup.get(0).getOwner()!=null && armyGroup.size()>0) 
    			{
    				System.out.println(armyGroup.get(0).getOwner().getPlayer()+" is Victorious!");
    				for(Army army : armyGroup)
    				{
	    				System.out.println(army.getName());
	    				helper.outputArmyUnits(army);
	    				System.out.println();
    				}
    				System.out.println();
    			}
    	}
	}
	
	//Controls for land battle sim
	private void selectLand(Scanner in)
	{
		boolean isDone = false;
	    String input = " ";
	    while(input.charAt(0) != 'e' && !isDone)
	    {
	    	System.out.println();
			System.out.println("Day: "+landCombat.day());
			System.out.println("0: Display Armies");
			System.out.println("1: Display Army");
			System.out.println("2: Progress One Day");
			System.out.println("e: Exit");

	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Display All Armies (Not Full Display)
	    	case '0':
	    		displayArmies();
	    		break;
	    		
	    	//Display One Army
	    	case '1':
	    		displayArmy(in);
	    		break;
	    	
	    	//Progress One Day
	    	case '2':
	    		landCombat.update();
	    		break;

	    	//Exit
	    	case 'e':
	    		break;

	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		System.out.println();
	    		break;
	    	}
	    	
    		if(landCombat.complete()) { isDone = true; }
	    }
	}
	
	//Naval combat sim
	public void goNaval()
	{
		String input;
		
		//Combatants number
    	System.out.println();
		System.out.print("Enter Number of Combatants (2-n): ");
    	int numCombatants = in.nextInt();
    	System.out.println();
    	
    	//Planet established
		this.planet = new Planet(0, 0, 0, "Battleground", game.getClimates().get(0), 0, 0, 0, 0);

    	//Army Groups creation
    	ArrayList<ArrayList<Fleet>> fleetGroups = new ArrayList<ArrayList<Fleet>>();
    	for(int i = 0; i < numCombatants; i++)
    	{
    		Faction faction = new Faction((long) i, game, "Faction "+i, "", 0);
    		fleetGroups.add(new ArrayList<Fleet>());
    		
    		//Selects number of armies
        	System.out.println();
    		System.out.print("Enter Number of Fleets for Faction "+i+" (1-n): ");
        	int numFleets = in.nextInt();
        	System.out.println();
        	
        	//Fleets creation
        	for(int j = 0; j < numFleets; j++)
        	{
        		Fleet fleet = new Fleet(faction, new ArrayList<NavalUnit>(), (Planet) null);
        		faction.addFleet(fleet);
        		
			    input = " ";
			    while(input.charAt(0) != 'e')
			    {
			    	System.out.println();
			    	System.out.println("Fleet "+j+" of "+faction.getPlayer());
			    	System.out.println("Ships: "+fleet.getShips().size());
					System.out.println("0: Display Ships");
					System.out.println("1: Add Ship");
					System.out.println("2: Remove Ship");
					System.out.println("3: Output Ship Stats");
					System.out.println("e: Continue");
		
			    	input = in.next();
			    	
			    	switch(input.charAt(0))
			    	{
			    	//Display Ships
			    	case '0':
			    		helper.outputFleetShips(fleet);
			    		break;
			    		
			    	//Add Ship
			    	case '1':
			    		addShip(fleet, in);
			    		break;
			    	
			    	//Remove Ship
			    	case '2':
			    		removeShip(fleet, in);
			    		break;

			    	//Output Ship Templates
			    	case '3':
			    		for(NavalUnitTemplate temp : navalTemps) { helper.outputNavalUnit(temp); }
			    		break;
		
			    	//Continue
			    	case 'e':
			    		break;
		
			    	default:
			    		System.out.println();
			    		System.out.println("Invalid Input");
			    		System.out.println();
			    		break;
			    	}
			    }
			    
			    fleetGroups.get(i).add(fleet);
        	}
    	}
    	
    	//Places Faction i on planet, starts land combat
    	planet.setOwner(fleetGroups.get(0).get(0).getOwner());
    	for(Fleet fleet : fleetGroups.get(0)) { planet.addFleet(fleet); }
    	planet.beginNavalCombat(fleetGroups.get(1).get(0));
    	navalCombat = planet.getNavalCombat();
    	for(int i = 1; i < fleetGroups.size(); i++) 
    	{ 
    		for(int j = 0; j < fleetGroups.get(i).size(); j++)
    		{
    			if((i==1 && j!= 0) || (i!=1 && j==0)) { navalCombat.addFleet(fleetGroups.get(i).get(j));} 
    		}
    	}
    	
    	selectNaval(in);
    	
    	for(ArrayList<Fleet> fleetGroup : fleetGroups) { 
    		if(fleetGroup.get(0).getOwner()!=null && fleetGroup.size()>0) 
    			{
    				System.out.println(fleetGroup.get(0).getOwner().getPlayer()+" is Victorious!");
    				for(Fleet fleet : fleetGroup)
    				{
	    				System.out.println(fleet.getName());
	    				helper.outputFleetShips(fleet);
	    				System.out.println();
    				}
    				System.out.println();
    			}
    	}
	}

	//Controls for navla battle sim
	private void selectNaval(Scanner in)
	{
		boolean isDone = false;
	    String input = " ";
	    while(input.charAt(0) != 'e' && !isDone)
	    {
	    	System.out.println();
			System.out.println("Day: "+navalCombat.day());
			System.out.println("Phase: "+navalCombat.phase());
			System.out.println("0: Display Fleets");
			System.out.println("1: Display Fleet");
			System.out.println("2: Progress One Day");
			System.out.println("e: Exit");

	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Display All Fleets (Not Full Display)
	    	case '0':
	    		displayFleets();
	    		break;
	    		
	    	//Display One Fleet
	    	case '1':
	    		displayFleet(in);
	    		break;
	    	
	    	//Progress One Day
	    	case '2':
	    		navalCombat.update();
	    		break;

	    	//Exit
	    	case 'e':
	    		break;

	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		System.out.println();
	    		break;
	    	}
	    	
    		if(navalCombat.complete()) { isDone = true; }
	    }
	}
	
	//Adds unit to army
	private void addUnit(Army army, Scanner in)
	{
		for(LandUnitTemplate temp : landTemps) { System.out.println(temp.getID()+" "+temp.getType()); }
		
		System.out.println();
		System.out.print("Select a unit to add (by ID): ");
		int unitId = in.nextInt();

		System.out.println();
		System.out.print("Choose how many: ");
		
		for(int i = in.nextInt(); i > 0; i--) { army.addUnit(new LandUnit(landTemps.get(unitId))); }
	}
	
	//Removes unit from army
	private void removeUnit(Army army, Scanner in)
	{
		for(int i = 0; i < army.getUnits().size(); i++)
		{
			LandUnit unit = army.getUnits().get(i);
			System.out.println(i+" "+unit.getType());
		}

		System.out.println();
		System.out.print("Select a unit to remove (by index): ");
		army.removeUnit(army.getUnit(in.nextInt()));
	}
	
	//Adds ship to fleet
	private void addShip(Fleet fleet, Scanner in)
	{
		for(NavalUnitTemplate temp : navalTemps) { System.out.println(temp.getID()+" "+temp.getType()); }
		
		System.out.println();
		System.out.print("Select a ship to add (by ID): ");
		int shipId = in.nextInt();

		System.out.println();
		System.out.print("Choose how many: ");
		
		for(int i = in.nextInt(); i > 0; i--) { fleet.addShip(new NavalUnit(navalTemps.get(shipId))); }
	}
	
	//Removes unit from fleet
	private void removeShip(Fleet fleet, Scanner in)
	{
		for(int i = 0; i < fleet.getShips().size(); i++)
		{
			NavalUnit ship = fleet.getShips().get(i);
			System.out.println();
			System.out.println(i+" "+ship.getType());
		}

		System.out.println();
		System.out.print("Select a ship to remove (by index): ");
		fleet.removeShip(fleet.getShip(in.nextInt()));
	}
	
	//Displays List of All Armies
	private void displayArmies()
	{
		ArrayList<ArrayList<Army>> armyGroups = landCombat.armies();
		for(ArrayList<Army> armyGroup : armyGroups)
		{
			System.out.println(armyGroup.get(0).getOwner().getPlayer());
			for(Army army : armyGroup) 
			{ 
				System.out.println(army.getName());
				System.out.println("   Units: "+army.getUnits().size());
			}
			System.out.println();
		}
	}
	
	//Fully displays an army
	private void displayArmy(Scanner in)
	{
		for(Faction faction : landCombat.combatants()) { System.out.println(faction.getID()+" "+faction.getPlayer()); }
		
		System.out.println();
		System.out.print("Select a faction: ");
		int factionIdx = in.nextInt();
		System.out.println();
		
		for(int i = 0; i < landCombat.armies().get(factionIdx).size(); i++) 
		{ 
			Army army = landCombat.armies().get(factionIdx).get(i);
			System.out.println(i+" "+army.getName());
		}

		System.out.println();
		System.out.print("Select an army: ");
		int armyIdx = in.nextInt();
		System.out.println();

		System.out.print(landCombat.armies().get(factionIdx).get(armyIdx).getName());
		helper.outputArmyUnits(landCombat.armies().get(factionIdx).get(armyIdx));
	}

	//Displays List of All Fleets
	private void displayFleets()
	{
		ArrayList<ArrayList<Fleet>> fleetGroups = navalCombat.fleets();
		for(ArrayList<Fleet> fleetGroup : fleetGroups)
		{
			System.out.println(fleetGroup.get(0).getOwner().getPlayer());
			for(Fleet fleet : fleetGroup) 
			{ 
				System.out.println(fleet.getName());
				System.out.println("   Ships: "+fleet.getShips().size());
			}
			System.out.println();
		}
	}
	
	//Fully displays a fleet
	private void displayFleet(Scanner in)
	{
		for(Faction faction : navalCombat.combatants()) { System.out.println(faction.getID()+" "+faction.getPlayer()); }
		
		System.out.println();
		System.out.print("Select a faction: ");
		int factionIdx = in.nextInt();
		System.out.println();
		
		for(int i = 0; i < navalCombat.fleets().get(factionIdx).size(); i++) 
		{ 
			Fleet fleet = navalCombat.fleets().get(factionIdx).get(i);
			System.out.println(i+" "+fleet.getName());
		}

		System.out.println();
		System.out.print("Select an fleet: ");
		int fleetIdx = in.nextInt();
		System.out.println();

		System.out.print(navalCombat.fleets().get(factionIdx).get(fleetIdx).getName());
		helper.outputFleetShips(navalCombat.fleets().get(factionIdx).get(fleetIdx));
	}
}
