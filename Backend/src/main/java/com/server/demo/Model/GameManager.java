package com.server.demo.Model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import com.server.demo.Entities.User;
import com.server.demo.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.server.demo.Entities.Session;
import com.server.demo.Game.Army;
import com.server.demo.Game.Faction;
import com.server.demo.Game.Fleet;
import com.server.demo.Game.Game;
import com.server.demo.Game.LandUnitTemplate;
import com.server.demo.Game.NavalUnit;
import com.server.demo.Game.NavalUnitTemplate;
import com.server.demo.Game.Planet;
import com.server.demo.Game.Hyperlane;
import com.server.demo.Game.LandUnit;
import org.springframework.stereotype.Service;

//Note: this model is only the data access layer, it is not responsible for all game activity
@Service
public class GameManager {

	ArrayList<Game> _activeGames = new ArrayList<>();
	//Game game;

	//@Autowired
	//GameRepository repo;

	private GMTools tools;

	//Starts Game
	public void startGame(Session gameSession)
	{
		Game game = null;

		try {
			game = new Game(gameSession);
			_activeGames.add(game);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		tools = new GMTools(game);
		
		//Randomly places players onto a single planet
		Random rng = new Random();
		int numFactions = game.getFactions().size()-1;
		int space = game.getPlanets().size()/numFactions;
		ArrayList<Integer> unStarted = new ArrayList<Integer>();
		for(int i = 1; i < numFactions+1; i++) { unStarted.add(i); }
		
		for(int i = 0; i < numFactions; i++)
		{
			int next = rng.nextInt(unStarted.size());
			game.startpos(game.getFactions().get(unStarted.get(next)), game.getPlanets().get(i*space));
			unStarted.remove(next);
		}
		
		game.fillNeutral();
	}
	
	//Returns a game instance
	public Game getGameById(long id)
	{
		for (Game g:
			 _activeGames) {
			if (g.getID() == id)
				return g;
		}

		return null;
	}

	public ArrayList<Game> getActiveGames(){
		return _activeGames;
	}

	/*
	 * Army
	 */
	
	//Request to move army (of armyID) to planet (of planetID) by player (of playerID)
	public void armyMove(long playerID, long armyID, long planetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Army army = tools.findArmyByID(armyID, faction);
		Planet planet = tools.findPlanetByID(planetID);

		army.embark();
		fleetMoveTo(army.getTransport(), planet);
	}
	
	//Embarks given army
	public void armyEmbark(long playerID, long armyID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Army army = tools.findArmyByID(armyID, faction);

		army.embark();
	}
	
	//Merges army B into army A
	public void armyMerge(long playerID, long aID, long bID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Army armyA = tools.findArmyByID(aID, faction);
		Army armyB = tools.findArmyByID(bID, faction);
		
		armyA.merge(armyB);
	}
	
	//Splits off given array of units into new army
	public void armySplit(long playerID, long armyID, int unitID[])
	{
		Faction faction = tools.findFactionByID(playerID);
		Army army = tools.findArmyByID(armyID, faction);
		
		ArrayList<LandUnit> units = new ArrayList<LandUnit>();
		for(int ID : unitID)
		{
			units.add(tools.findUnitByID(ID, army));
		}
		
		army.split(units);
	}
	
	//Disbands given army
	public void armyDisband(long playerID, long armyID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Army army = tools.findArmyByID(armyID, faction);
		army.disband();
	}
	
	//Disbands given army
	public void unitDisband(long playerID, long armyID, long unitID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Army army = tools.findArmyByID(armyID, faction);
		LandUnit unit = tools.findUnitByID(unitID, army);
		unit.disband();
	}

	/*
	 * Fleet
	 */

	//Request to move fleet (of fleetID) to planet (of planetID) by player (of playerID)
	//NOTE - Right-click, clears any moves queued
	public void fleetMove(long playerID, long fleetID, long planetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		Planet planet = tools.findPlanetByID(planetID);

		fleetMoveTo(fleet, planet);
	}

	//Clears fleet's move queue, then queues up moves in fleet's moveQueue along shortest path
	private void fleetMoveTo(Fleet fleet, Planet target)
	{
		ArrayList<Planet> path = tools.fleetMovePath(fleet, target);
		
		fleet.moveTo(path.get(0));
		
		for(int i = 1; i < path.size(); i++) { fleet.queueMove(path.get(i)); }
	}
	
	//Adds move to planet to fleet's move queue
	//NOTE - Shift+Right-click, just adds move/s to queue
	public void fleetMoveQueue(long playerID, long fleetID, long planetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		Planet planet = tools.findPlanetByID(planetID);
		
		fleetMoveToQueue(fleet, planet);
	}

	//Queues up moves in fleet's moveQueue along shortest path
	private void fleetMoveToQueue(Fleet fleet, Planet target)
	{
		ArrayList<Planet> path = tools.fleetMovePath(fleet, target);
		for(Planet planet : path) { fleet.queueMove(planet); }
	}
	
	//Cancels fleet moves
	public void fleetMoveCancel(long playerID, long fleetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		
		fleet.cancelMove();
	}
	
	//Disembarks fleet
	public void fleetDisembark(long playerID, long fleetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		fleet.disembark();
	}
	
	//Merges fleet B into fleet A
	public void fleetMerge(long playerID, long aID, long bID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleetA = tools.findFleetByID(aID, faction);
		Fleet fleetB = tools.findFleetByID(bID, faction);
		
		fleetA.merge(fleetB);
	}

	//Splits off given array of ships into new fleet
	public void fleetSplit(long playerID, long fleetID, int shipID[])
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		
		ArrayList<NavalUnit> ships = new ArrayList<NavalUnit>();
		for(int ID : shipID)
		{
			ships.add(tools.findShipByID(ID, fleet));
		}
		
		fleet.split(ships);
	}
	
	//Disbands given fleet
	public void fleetDisband(long playerID, long fleetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		fleet.disband();
	}
	
	//Disbands given ship
	public void shipDisband(long playerID, long fleetID, long shipID)
	{
		Faction faction = tools.findFactionByID(playerID);
		Fleet fleet = tools.findFleetByID(fleetID, faction);
		NavalUnit ship = tools.findShipByID(shipID, fleet);
		ship.disband();
	}
	
	/*
	 * Planet
	 */
	
	//Request to make a unit (of typeID) at planet (of planetID) by player (of playerID)
	public void buildUnit(long playerID, long typeID, long planetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		LandUnitTemplate type = tools.findUnitTypeByID(typeID, faction);
		Planet planet = tools.findPlanetByID(planetID);
		
		planet.buildLandUnit(type);
	}

	//Request to cancel a queue'd unit at planet (of planetID)
	public void buildUnitCancel(long planetID)
	{
		Planet planet = tools.findPlanetByID(planetID);
		
		planet.cancelLandUnit();
	}
	
	//Request to make a ship (of typeID) at planet (of planetID) by player (of playerID)
	public void buildShip(long playerID, long typeID, long planetID)
	{
		Faction faction = tools.findFactionByID(playerID);
		NavalUnitTemplate type = tools.findShipTypeByID(typeID, faction);
		Planet planet = tools.findPlanetByID(planetID);
		
		planet.buildNavalUnit(type);
	}

	//Request to cancel a queue'd ship at planet (of planetID)
	public void buildShipCancel(long planetID)
	{
		Planet planet = tools.findPlanetByID(planetID);
		
		planet.cancelNavalUnit();
	}
}
