package com.server.demo.Game;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;

import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;

/*
 * Main game object
 * Contains all data pertaining to the game
 * Most calculation methods should remain outside of this class to simplify data transfer
 */
public class Game {

	private long id;
	private SimpMessagingTemplate template;
	private static String corePath;
	private Timer timer;
	private int day;	//Keeps track of what day interval it is
	private ArrayList<Faction> factions = new ArrayList<Faction>();	//List of player factions
	private ArrayList<Planet> planets = new ArrayList<Planet>();	//Game Map
	private ArrayList<Climate> climates = new ArrayList<Climate>(); //List of climates

	//for discovery lookup
	private String gameName;
	private int startMoney;
	private int startPoint;

	//Constructor imports session data and initializes the core of the game using it
	public Game(Session session) throws FileNotFoundException
	{
		this.id = session.getID();
		this.gameName = session.getName();
		this.startMoney = session.getStartingMoney();
		this.startPoint = session.getStartingPoints();
		this.template = new SimpMessagingTemplate(new ExecutorSubscribableChannel());

		//Determines file path in retrieving object template data
		Path currentRelativePath = Paths.get("");
		corePath = currentRelativePath.toAbsolutePath().toString() + "\\src\\main\\resources\\templates\\gaw";

		//Initial day is day 0
		day = 0;
		
		//Adds players from session to players list
		factions.add(new Faction(-1, this, "Neutral Systems", "#DAE6F2FF", 0));	//Neutral faction
		factions.add(new Faction(session.getHost().getId(), this, session.getHost().getName(), 
								session.getColor(session.getHost()), 
								session.getStartingMoney()));	//Host player faction
		//Guest player factions
		for(User player : session.getGuests())
		{
			factions.add(new Faction(player.getId(), this, player.getName(), 
					session.getColor(player),
					session.getStartingMoney()));
		}
		
		//Generates Land Unit Templates
		long landTempID = 0;
		ArrayList<LandUnitTemplate> landTemplates = GameHelper.generateLandTemplates(corePath, factions.get(0));
		for(Faction faction : factions)
		{
			//Makes a copy of each land unit template, adds to current faction's list of templates
			for(LandUnitTemplate temp : landTemplates)
			{
				faction.addLandTemplate(new LandUnitTemplate(landTempID, faction, temp));
				landTempID++;
			}
		}

		//Generates Naval Unit Templates
		long navalTempID = 0;
		ArrayList<NavalUnitTemplate> navalTemplates = GameHelper.generateNavalTemplates(corePath, factions.get(0));
		for(Faction faction : factions)
		{
			//Makes a copy of each naval unit template, adds to current faction's list of templates
			for(NavalUnitTemplate temp : navalTemplates)
			{
				faction.addNavalTemplate(new NavalUnitTemplate(navalTempID, faction, temp));
				navalTempID++;
			}
		}
		
		//Generates the Climates
		climates = GameHelper.generateClimates(corePath);
		
		//Generates the map
		planets = GameHelper.generateMap(corePath, session.getPlanetCount(), climates);
	}

	public String getName(){
		return gameName;
	}

	public int getStartMoney(){
		return startMoney;
	}

	public int getStartPoint(){
		return startPoint;
	}

	//Initial Game setup
	//Players choose their start positions
	//Remaining is given to neutral faction
	//Returns cost of planet
	public int startpos(Faction owner, Planet planet)
	{
		planet.setOwner(owner);

		planet.produceLandUnit(new LandUnit(owner.getLandTemplate("Militia Corps")));
		planet.produceLandUnit(new LandUnit(owner.getLandTemplate("Militia Corps")));
		planet.produceLandUnit(new LandUnit(owner.getLandTemplate("Militia Corps")));
		planet.produceLandUnit(new LandUnit(owner.getLandTemplate("Militia Corps")));
		while(planet.getArmies().size()>1)
		{
			planet.getArmies().get(0).merge(planet.getArmies().get(1));
		}

		planet.produceNavalUnit(new NavalUnit(owner.getNavalTemplate("Heavy Cruiser")));
		planet.produceNavalUnit(new NavalUnit(owner.getNavalTemplate("Light Cruiser")));
		planet.produceNavalUnit(new NavalUnit(owner.getNavalTemplate("Light Cruiser")));
		planet.produceNavalUnit(new NavalUnit(owner.getNavalTemplate("Light Cruiser")));
		while(planet.getFleets().size()>1)
		{
			planet.getFleets().get(0).merge(planet.getFleets().get(1));
		}
		
		return GameHelper.costPlanet(planet);
	}
	
	//Immediately after initial game setup
	//Fills in remaining planets with neutral faction
	public void fillNeutral()
	{
		Faction neutral = factions.get(0);
		for(Planet planet : planets)
		{
			//If no owner, gives to neutral faction and adds a garrison/fleet
			if(planet.getOwner()==null)
			{
				int cost = startpos(neutral, planet);
				
				ArrayList<LandUnit> units = GameHelper.fillArmy(cost, neutral);
				for(LandUnit unit : units) {  }
				while(planet.getArmies().size()>1)
				{
					planet.getArmies().get(0).merge(planet.getArmies().get(1));
				}
				
				ArrayList<NavalUnit> ships = GameHelper.fillFleet(cost, neutral);
				for(NavalUnit ship : ships) { planet.produceNavalUnit(ship); }
				while(planet.getFleets().size()>1)
				{
					planet.getFleets().get(0).merge(planet.getFleets().get(1));
				}
			}
		}
	}
	
	//Starts Timer thread
	public void startTimer()
	{
		this.timer = new Timer(this);
		Thread threadTimer = new Thread(timer, "threadTimer");
		threadTimer.start();
	}
	
	//Stops Timer thread
	public void stopTimer()
	{
		timer.stop();
	}
	
	/*
	 * Update Method
	 * Updates include:
	 * 		- Day counter
	 * 		- Faction resources
	 * 		- Moves
	 * 		- HP Regeneration
	 * 		- Planet production
	 * 		- Combat
	 */
	public void update()
	{
		day++;
		
		for(Faction faction : factions)
		{
			for(Fleet fleet : faction.getFleets()) { fleet.update(); }
		}

		for(Planet planet : planets) 
		{ 
			planet.update(); 
			for(Hyperlane lane : planet.getHyperlanes())
			{
				//Updates lane only if planet if pt A, prevents updating twice
				if(lane.getA().equals(planet)) { lane.update(); }
			}
		}
	}
	
	//Receives an update message and
	public void outputMessage(UpdateMessage message)
	{
        this.template.convertAndSend("/broadcast/"+id, message);
        
        //TODO - testing
//        String mess = message.toString();
//        if(!mess.contains("mod") && !mess.contains("HP")) { System.out.println(message.toString()); }
//        System.out.println(message.toString());
	}
	
	/*
	 * Getters and Setters
	 */

	public long getID() { return id; }
	public String getCoreDirectory() { return corePath; }
	public int getDay() { return day; }
	public ArrayList<Faction> getFactions() { return factions; }
	public ArrayList<Planet> getPlanets() { return planets; }
	public ArrayList<Climate> getClimates() { return climates; }
}
