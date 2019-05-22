package com.server.demo.Game;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;

public class LogicTest {
	static GameManagerTest test;
	static LogicTestHelper helper;
	static Game game;
	static Faction me;
	static long meID;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Session session = new Session(new User("Warlord Beelzebub the Bloodthirster", "abc"), "Test Game", 1, 20, 
				2000, 500);
		session.addGuest(new User("Brutebear Face-Clawer", "abc"));
		session.addGuest(new User("Ilthigald the Eternal", "abc"));
		session.addGuest(new User("Me-meow", "meow"));
		test = new GameManagerTest(session);
	    game = test.getGame();
	    helper = new LogicTestHelper(test, game);
	    
	    System.out.println();
	    System.out.println("Starting game...");
	    System.out.println();
	    
	    //Gets Player stuff
	    me = game.getFactions().get(1);
	    meID = me.getID();

	    Scanner in = new Scanner(System.in);
	    
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
	    	System.out.println();
			System.out.println("Select what to run");
			System.out.println("0: Game");
			System.out.println("1: Land Combat");
			System.out.println("2: Naval Combat");
			System.out.println("e: Exit");

	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Game
	    	case '0':
	    		runGame(in);
	    		break;
	    		
	    	//Land Combat
	    	case '1':
	    		(new LogicTestCombat(game, in, helper)).goLand();
	    		break;
	    	
	    	//Naval Combat
	    	case '2':
	    		(new LogicTestCombat(game, in, helper)).goNaval();
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
	    }

	    in.close();
	}
	
	//Runs a text-based singleplayer version of the game
	private static void runGame(Scanner in)
	{
	    game.startTimer();
	    System.out.println();
	    System.out.println("Game started...");
	    System.out.println();
	    
	    //Text-based playable game interface
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
	    	System.out.println();
	    	helper.topBar();
			System.out.println("Select an entity type to list and then select from ('e' to end game)");
			System.out.println("0: Planets");
			System.out.println("1: Factions");
			System.out.println("2: Player Planets");
			System.out.println("3: Player Armies");
			System.out.println("4: Player Fleets");
			System.out.println("5: Player Unit Types");
			System.out.println("6: Player Ship Types");
			System.out.println("7: Map");
	    	
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Planets
	    	case '0':
	    		listPlanets(game.getPlanets(), in);
	    		break;

	    	//Factions
	    	case '1':
	    		helper.outputFactionList();
	    		break;

	    	//Player Planets
	    	case '2':
	    		listPlanets(me.getPlanets(), in);
	    		break;
		    		
	    	//Player Armies
	    	case '3':
	    		if(helper.activeArmies(me.getArmies())>0)
	    			listArmies(me.getArmies(), in);
	    		else
	    			System.out.println("No armies\n");
	    		break;
		    
	    	//Player Fleets
	    	case '4':
	    		if(helper.activeFleets(me.getFleets())>0)
	    			listFleets(me.getFleets(), in);
	    		else
	    			System.out.println("No fleets\n");
	    		break;	
 
	    	//Player Unit Types
	    	case '5':
	    		helper.outputUnitTypesList(me);
	    		break;	    		

	    	//Player Ship Types
	    	case '6':
	    		helper.outputShipTypesList(me);
	    		break;	    		

	    	//Map
	    	case '7':
	    		helper.outputMap();
	    		break;
		    		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		System.out.println();
	    		break;
	    	}
	    }
	    
	    //Ends game
	    game.stopTimer();

	    System.out.println();
	    System.out.println("Game Ended");
	}

	//Command - List All (a)
	private static void listPlanets(ArrayList<Planet> planets, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select a planet ('e' to return)");
			helper.outputPlanetList(planets);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 0; i < planets.size() && !found; i++)
	    		{
	    			Planet c = planets.get(i);
	    			if(idx == (int) c.getID()) { idx = i; found = true; }
	    		}
	    		
	    		if(found) { selectPlanet(planets.get(idx), in); input = "e"; }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	}
	
	//Selects planet
	private static void selectPlanet(Planet c, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println(c.getID()+" "+c.getName());
			System.out.println("Select a planet command ('e' to return)");
			System.out.println("i: Planet Info");
			System.out.println("l: Planet Armies");
			System.out.println("n: Planet Fleets");
			System.out.println("b: Build Unit");
			System.out.println("x: Cancel Queued Unit");
			System.out.println("c: Construct Fleet");
			System.out.println("v: Cancel Queued Ship");
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Info
	    	case 'i':
	    		helper.outputPlanet(c);
	    		break;
	    		
	    	//Armies
	    	case 'l':
	    		if(c.getArmies().size()>0)
	    			listArmies(c.getArmies(), in);
	    		else
	    			System.out.println("No armies\n");
	    		break;
	    		
	    	//Fleets
	    	case 'n':
	    		if(c.getFleets().size()>0)
	    			listFleets(c.getFleets(), in);
	    		else
	    			System.out.println("No fleets\n");
	    		break;

	    	//Build Unit
	    	case 'b':
	    		LandUnitTemplate unit = selectBuildUnit(c.getOwner(), in);
	    		if(unit!=null) { test.buildUnit(c.getOwner().getID(), unit.getID(), c.getID()); }
	    		break;
	    		
	    	//Cancel Queued Unit
	    	case 'x':
	    		test.buildUnitCancel(c.getID());
	    		break;

	    	//Build Ship
	    	case 'c':
	    		NavalUnitTemplate ship = selectBuildShip(c.getOwner(), in);
	    		if(ship!=null) { test.buildShip(c.getOwner().getID(), ship.getID(), c.getID()); }
	    		break;
	    		
	    	//Cancel Queued Ship
	    	case 'v':
	    		test.buildShipCancel(c.getID());
	    		break;
		    		
	    	//Return
	    	case 'e':
	    		break;
	    		
	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		break;
	    	}
	    }
	}

	//Selects a unit to build
	private static LandUnitTemplate selectBuildUnit(Faction faction, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
	    	ArrayList<LandUnitTemplate> temps = faction.getLandTemplates();
			System.out.println();
			helper.topBar();
			System.out.println("Select a unit to build ('e' to return)");
			helper.outputUnitTypesList(faction);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 1; i < temps.size(); i++)
	    		{
	    			if(idx == (int) temps.get(i).getID()) { found = true; }
	    		}
	    		if(found 
	    	    		&& !temps.get(idx%temps.size()).getType().equals("MISSING NAME")
	    	    		&& !temps.get(idx%temps.size()).getTransportType().equals("none"))
	    	    		{ return temps.get(idx%temps.size()); }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	    
		return null;
	}

	//Selects a unit to build
	private static NavalUnitTemplate selectBuildShip(Faction faction, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
	    	ArrayList<NavalUnitTemplate> temps = faction.getNavalTemplates();
			System.out.println();
			helper.topBar();
			System.out.println("Select a unit to build ('e' to return)");
			helper.outputShipTypesList(faction);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 1; i < temps.size(); i++)
	    		{
	    			if(idx == (int) temps.get(i).getID()) { found = true; }
	    		}
	    		
	    		if(found 
	    		&& !temps.get(idx%temps.size()).getType().equals("MISSING NAME")
	    		&& !temps.get(idx%temps.size()).getCargoType().equals("none"))
	    		{ return temps.get(idx%temps.size()); }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	    
		return null;
	}
	
	//Lists armies, can select one of the listed
	private static void listArmies(ArrayList<Army> armies, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select an army ('e' to return)");
			helper.outputArmyList(armies);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 0; i < armies.size() && !found; i++)
	    		{
	    			if(idx == (int) armies.get(i).getID()) { idx = i; found = true; }
	    		}
	    		
	    		if(found) { selectArmy(armies.get(idx), in); input = "e"; }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	}
	
	//Army selected, can issue commands
	private static void selectArmy(Army c, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println(c.getID()+" "+c.getName());
			System.out.println("Select an army command ('e' to return)");
			System.out.println("i: Army Info");
			System.out.println("u: Army Units");
			System.out.println("t: Move to a Planet");
			System.out.println("x: Embark");
			System.out.println("m: Merge with a neighboring army");
			System.out.println("s: Split into a new army");
			System.out.println("d: Disband");
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Info
	    	case 'i':
	    		helper.outputArmy(c);
	    		break;
	    		
	    	//Units
	    	case 'u':
	    		helper.outputArmyUnits(c);
	    		break;
	    		
	    	//Move
	    	case 't':
	    		Planet dest = selectMove(in);
	    		if(dest!=null) { test.armyMove(c.getOwner().getID(), c.getID(), dest.getID()); }
	    		selectFleet(c.getTransport(), in);
	    		input = "e";
	    		break;

	    	//Embark
	    	case 'x':
	    		test.armyEmbark(c.getOwner().getID(), c.getID());
	    		selectFleet(c.getTransport(), in);
	    		input = "e";
	    		break;
		    		
	    	//Merge
	    	case 'm':
	    		selectArmyMerge(c, c.getStation().getArmies(), in);
	    		break;

	    	//Split
	    	case 's':
	    		break;

	    	//Disband
	    	case 'd':
	    		test.armyDisband(c.getOwner().getID(), c.getID());
	    		input = "e";
	    		break;
		    		
	    	//Return
	    	case 'e':
	    		break;
	    		
	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		break;
	    	}
	    }
	}
	
	//Lists local armies, select which army to merge with
	private static void selectArmyMerge(Army c, ArrayList<Army> armies, Scanner in)
	{
		String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select an army to merge with ('e' to return)");
			helper.outputArmyList(armies);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 0; i < armies.size() && !found; i++)
	    		{
	    			if(idx == (int) armies.get(i).getID() && idx != c.getID()) { idx = i; found = true; }
	    		}
	    		
	    		if(found) { test.armyMerge(c.getOwner().getID(), c.getID(), armies.get(idx).getID()); }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	}

	//Lists armies, can select one of the listed
	private static void listFleets(ArrayList<Fleet> fleets, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select a fleet ('e' to return)");
			helper.outputFleetList(fleets);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 0; i < fleets.size() && !found; i++)
	    		{
	    			if(idx == (int) fleets.get(i).getID()) { idx = i; found = true; }
	    		}
	    		
	    		if(found) { selectFleet(fleets.get(idx), in); input = "e"; }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	}
	
	//Army selected, can issue commands
	private static void selectFleet(Fleet c, Scanner in)
	{
	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println(c.getID()+" "+c.getName());
			System.out.println("Select a fleet command ('e' to return)");
			System.out.println("i: Fleet Info");
			System.out.println("u: Fleet Ships");
			System.out.println("t: Move to a Planet");
			System.out.println("x: Disembark");
			System.out.println("m: Merge with a nearby fleet");
			System.out.println("s: Split into a new fleet");
			System.out.println("d: Disband");
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{
	    	//Info
	    	case 'i':
	    		helper.outputFleet(c);
	    		break;
	    		
	    	//Ships
	    	case 'u':
	    		helper.outputFleetShips(c);
	    		break;
	    		
	    	//Move
	    	case 't':
	    		Planet dest = selectMove(in);
	    		if(dest!=null) { test.fleetMove(c.getOwner().getID(), c.getID(), dest.getID()); }
	    		break;

	    	//Embark
	    	case 'x':
	    		test.fleetDisembark(c.getOwner().getID(), c.getID());
	    		selectArmy(c.getCargo(), in);
	    		input = "e";
	    		break;
		    		
	    	//Merge
	    	case 'm':
	    		if(c.getStation()!=null)
	    			selectFleetMerge(c, c.getStation().getFleets(), in);
	    		else
	    			input = " ";
	    		break;

	    	//Split
	    	case 's':
	    		break;

	    	//Disband
	    	case 'd':
	    		test.fleetDisband(c.getOwner().getID(), c.getID());
	    		input = "e";
	    		break;
		    		
	    	//Return
	    	case 'e':
	    		break;
	    		
	    	default:
	    		System.out.println();
	    		System.out.println("Invalid Input");
	    		break;
	    	}
	    }
	}

	//Lists local fleets, select which army to merge with
	private static void selectFleetMerge(Fleet c, ArrayList<Fleet> fleets, Scanner in)
	{
		String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select a fleet to merge with ('e' to return)");
			helper.outputFleetList(fleets);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(int i = 0; i < fleets.size() && !found; i++)
	    		{
	    			if(idx == (int) fleets.get(i).getID() && idx != c.getID()) { idx = i; found = true; }
	    		}
	    		
	    		if(found) { test.fleetMerge(c.getOwner().getID(), c.getID(), fleets.get(idx).getID()); }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	}
	
	//Selects a planet to move to
	private static Planet selectMove(Scanner in)
	{
		ArrayList<Planet> planets = game.getPlanets();

	    String input = " ";
	    while(input.charAt(0) != 'e')
	    {
			System.out.println();
			helper.topBar();
			System.out.println("Select a planet to move to ('e' to return)");
			helper.outputPlanetList(planets);
			
	    	input = in.next();
	    	
	    	switch(input.charAt(0))
	    	{		
	    	//e
	    	case 'e':
	    		break;
	    		
	    	default:
	    		Scanner sc = new Scanner(input);
	    		int idx = -1;
	    		if(sc.hasNextInt()) { idx = sc.nextInt(); }
	    		
	    		boolean found = false;
	    		for(Planet c : planets)
	    		{
	    			if(idx == (int) c.getID()) { found = true; }
	    		}
	    		
	    		if(found) { return planets.get(idx); }
	    		else
	    		{
		    		System.out.println();
		    		System.out.println("Invalid Input");
	    		}
	    		sc.close();
	    		break;
	    	}
	    }
	    
		return null;
	}
}
