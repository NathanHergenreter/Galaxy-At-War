package com.server.demo.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

//Contains helper methods for Game
public class GameHelper {

	static final int MIN_DISTANCE = 3;	//Minimum distance a planet must be away from another planet
	static final int MAX_DISTANCE = 8;	//Maximum distance away a planet can be
										//for a hyperlane (unless a connection must be made)
	static final int RING_GROWTH = 10;	//Outward expansion of radius per ring
	
	static long armyCounter = 0;	//Army ID counter
	static long unitCounter = 0;	//Unit ID counter
	static long fleetCounter = 0;	//Fleet ID counter
	static long shipCounter = 0;	//Ship ID counter
	
	//Helper Method
	//Randomly generates a list of planets based on # of planets, planetCount
	public static ArrayList<Planet> generateMap(String corePath, int planetCount, ArrayList<Climate> climates) throws FileNotFoundException
	{	
		ArrayList<Planet> planets = new ArrayList<Planet>();
		long id = 0;

		//File contains possible planet names
		File file = new File(corePath+"\\names\\planet_names.txt"); 
		Scanner sc = new Scanner(file);
		ArrayList<String> names = new ArrayList<String>();
		
		//Accumlates all the names in the file into a list
		while(sc.hasNextLine())
		{
			names.add(sc.nextLine());
		}
		sc.close();
		
		//Generates a list of coords
		ArrayList<ArrayList<Double>> pts = generatePoints(planetCount);
		
		//Needs for creating a new Planet
		Random rng = new Random();
		String name;
		Climate climate;
		int size, roughness, mineralReserves, idx;
		double population;
		
		//Generates planetCount number of planets using randomly generated values
		//based upon a randomly chosen climate
		for(int i = 0; i < planetCount; i++)
		{
			//Picks a random name from list, name removed from list so no repeat
			idx = rng.nextInt(names.size());
			name = names.get(idx);
			names.remove(idx);
			
			//Randomly chooses a climate, coords, and size
			climate = climates.get(rng.nextInt(climates.size()));
			size = rng.nextInt(5);
			
			//Gets a random number from 1-1000
			//Multiplies by the climate habitability
			//Then by ((size+1)*2) / 10 to factor in size
			//Divides by 10 to allow numbers below one billion
			//Then rounds up to 2 decimal places
			population = ((double)rng.nextInt(1000)+1) * climate.getPopMod() 
					* ((double) ((size+1)*2)/10) / 10
						;
			population = Math.round(population*100.0)/100.0;

			//Gets a random number from maxRoughness - minRoughness
			//Adds to that value minRoughness to get a value between min and max
			roughness = rng.nextInt(climate.getMaxRoughness() - climate.getMinRoughness())
								+ climate.getMinRoughness();
			
			//Like above but with minerals
			mineralReserves = rng.nextInt(climate.getMaxMinerals() - climate.getMinMinerals())
								+ climate.getMinMinerals();

			//Adds newly defined planet to return list
			planets.add(new Planet(id, pts.get(i).get(0), pts.get(i).get(1), name, climate, size, roughness, population, mineralReserves));
			id++;
		}
		
		//Hyperlane Generation
		//Every planet within minimum distance is given a hyperlane between the two
		for(Planet ptA : planets)
		{
			for(Planet ptB : planets)
			{
				//Checks if planets are not the same
				if(ptA!=ptB)
				{
					//Euclidean distance between ptA and ptB
					double distance = Math.sqrt(((ptA.getX() - ptB.getX())*(ptA.getX()-ptB.getX()))
							+ ((ptA.getY() - ptB.getY())*(ptA.getY()-ptB.getY())));
					distance = Math.round(distance*100.0)/100.0;
					
					//Then, checks if planets are within maximum distance from each other
					//If so, adds a lane between them using the sqrt the calculated square as distance
					//and a random int from 1-5 as speed
					if( 
							distance <= MAX_DISTANCE )
					{
						boolean found = false;
						
						//Makes sure hyperlane has not already been added
						for(Hyperlane h : ptB.getHyperlanes())
						{
							if(h.getA()==ptA || h.getB()==ptA) { found = true; }
						}
						
						//If the hyperlane between A and B does not exist, adds it
						if(!found)
						{
							Hyperlane lane = new Hyperlane(ptA, ptB, distance, 
									rng.nextInt(5)+1);
							
							ptA.addHyperlane(lane);
							ptB.addHyperlane(lane);
						}
					}
				}
			}
		}
		
		//Performs a DFS and places planets into clusters of mutual reachability
		//If more than one cluster is made, then the minimum connection between 0 and the rest is found
		//Then, that closest cluster is merged into the first
		//Repeat until one cluster remains
		ArrayList<ArrayList<Planet>> clusters = new ArrayList<ArrayList<Planet>>();
		int clusterIdx=0;
		boolean found;
		
		//DFS Outer Loop
		for(Planet p : planets)
		{
			found = false;
			//Searches through cluster for planet
			for(ArrayList<Planet> cluster : clusters)
			{
				//If planet has already been added, sets found to true
				if(cluster.contains(p)) { found = true; break; };
			}
			
			//Skips if already in list
			//Otherwise, adds a new cluster to list and goes into DFS recursion
			if(!found)
			{
				clusters.add(new ArrayList<Planet>());
				DFSRecursion(p, clusters.get(clusterIdx));
				clusterIdx++;
			}
		}
		
		//Finds the minimum distance between groups and connects them
		while(clusters.size()>1)
		{
			int minIdxA=0;
			int minIdxB=0;
			int minClusterIdx=1;
			double minDist = Double.MAX_VALUE;
			
			//Goes through each cluster and finds that cluster closet cluster
			//then adds a connection from the closest planet to cluster 0
			for(int i = 1; i < clusters.size(); i++)
			{
				//Goes through each planet b in cluster i
				for(int b = 0; b < clusters.get(i).size(); b++)
				{
					Planet ptB = clusters.get(i).get(b);
					
					//Always looks for closest planet a in cluster 0 to planet b in i
					for(int a = 0; a < clusters.get(0).size(); a++)
					{
						Planet ptA = clusters.get(0).get(a);
						
						//Euclidean distance between ptA and ptB
						double distance = Math.sqrt(((ptA.getX() - ptB.getX())*(ptA.getX()-ptB.getX()))
								+ ((ptA.getY() - ptB.getY())*(ptA.getY()-ptB.getY())));
						distance = Math.round(distance*100.0)/100.0;
						
						//Checks if a new closest pair is found
						//Updates all new minimums
						if(distance < minDist)
						{
							minIdxA = a;
							minIdxB = b;
							minClusterIdx = i;
							minDist = distance;
						}
					}
				}
			}
			
			//Closest pair found at this point
			//Adds a lane between pair of planets in Cluster 0 and i, where A is in 0 and B in i
			Hyperlane lane = new Hyperlane(clusters.get(0).get(minIdxA), 
						clusters.get(minClusterIdx).get(minIdxB), minDist,
						rng.nextInt(5)+1);
			
			clusters.get(0).get(minIdxA).addHyperlane(lane);
			clusters.get(minClusterIdx).get(minIdxB).addHyperlane(lane);
			
			//Merges cluster i into 0
			while(clusters.get(minClusterIdx).size()>0)
			{
				clusters.get(0).add(clusters.get(minClusterIdx).get(0));
				clusters.get(minClusterIdx).remove(0);
			}
			//Cluster i is empty, removes
			clusters.remove(minClusterIdx);
		}
		
		return planets;
	}
	
	//Helper for generateMap
	//Uses an algorithm to generate points
	private static ArrayList<ArrayList<Double>> generatePoints(int count)
	{
		ArrayList<ArrayList<Double>> map = new ArrayList<ArrayList<Double>>();
		double x=0,y=0;
		int ring = 0, distance = -RING_GROWTH;
		boolean free;
		Random rng = new Random();
		
		//Creates a number of coord pairs equal to number of planets
		for(int i = 0; i<count; i++)
		{
			//Places planets in each ring, rings are a distance of 5 from each other
			if(ring==0) 
			{ 
				ring = rng.nextInt(3);
				distance+=RING_GROWTH;
				
				//Slowly increases the amount per ring
				//First inner ring has more than any other
				ring = distance > 2*RING_GROWTH ? ring+6
				          : distance > RING_GROWTH ? ring+8
				          : ring+4;
			}
			
			free = false;	//Set to false to enter loop
			
			//Loops until a pair of random coords is proven to be not taken already
			while(!free)
			{
				free = true;
			
				//Chooses a random position ring growth away from center for both x and y
				x = RING_GROWTH-rng.nextInt(RING_GROWTH*2);
				y = RING_GROWTH-rng.nextInt(RING_GROWTH*2);
				
				//Randomly chooses whether x, y, or both are to be placed distance away from center
				if(rng.nextInt(2)==0) 
				{ 
					x = (x > 0) ? x+distance : x-distance;
				}
				else if(rng.nextInt(2)==0) 
				{ 
					y = (y > 0) ? y+distance : y-distance;
				}
				else 
				{ 
					x = (x > 0) ? x+distance : x-distance;
					y = (y > 0) ? y+distance : y-distance;
				}
				
				//Goes through map checking each pair of coords
				//If another planet is within min distance, sets free to false
				//and tries again with new coords
				for(int k = 0; k<map.size(); k++)
				{
					//if(map.get(k).get(0)==x && map.get(k).get(1)==y) 
					if(Math.abs(map.get(k).get(0)-x) <= MIN_DISTANCE 
							&& Math.abs(map.get(k).get(1)-y) <= MIN_DISTANCE) 
					{ free = false; break; }
				}
			}
			
			//Adds the new point pair to the list
			map.add(new ArrayList<Double>());
			map.get(i).add(x);
			map.get(i).add(y);
			
			//Decrements remaining planets in ring
			ring--;
		}
		
		return map;
	}
	
	//Recursive Method for DFS
	//Updates clusters list whenever an unchecked planet is found
	private static void DFSRecursion(Planet p, ArrayList<Planet> cluster)
	{
		//Sets planet p as found and within current cluster
		cluster.add(p);
		
		//Goes through edges (lanes) and calls the recursion on the opposite end
		for(Hyperlane lane : p.getHyperlanes())
		{
			//Checks if lane's ptA is p
			if(lane.getA()==p)
			{
				//If so, then checks if ptB is already found
				if(!cluster.contains(lane.getB()))
				{
					//If not, then calls the recursion on B
					DFSRecursion(lane.getB(), cluster);
				}
			}
			//Otherwise, checks p is ptB
			//Checks if ptA is already founds
			else if(!cluster.contains(lane.getA()))
			{
				//If not, then calls the recursion on A
				DFSRecursion(lane.getA(), cluster);
			}
		}
	}
	
	//Goes to the climates directory, reads through the files, and creates the climates
	//as defined by these files
	public static ArrayList<Climate> generateClimates(String corePath) throws FileNotFoundException
	{
		ArrayList<Climate> climates = new ArrayList<Climate>();
		
		//Establishes Climates directory and Scanner
		File folder = new File(corePath+"\\climates"); 
		File files[] = folder.listFiles();
		Scanner sc; 
		String next = "";
		
		//Climate variables, before each file read set to default values
		String name;
		int minRoughness;
		int maxRoughness;
		double popMod;
		int minMinerals;
		int maxMinerals;
			  
		//Reads through all files in directory, generating climates based on
		//definitions in files
	    for(File file : files) 
	    {
	    	//Default climate values
	    	name = "MISSING NAME";
			minRoughness = 0;
			maxRoughness = 0;
			popMod = 0.0;
			minMinerals = 0;
			maxMinerals= 0;
			
	    	sc = new Scanner(file);
	    	
	    	//Scans through file line by line
	    	while(sc.hasNextLine())
	    	{
	    		next = sc.nextLine();
	    		
	    		//Checks if file is an ignored file, if so, ends scan
	    		if(next.equalsIgnoreCase("#IGNORE")) { break; }
	    		
	    		//Splits line by '=', should split into two
	    		String[] line = next.split("=");
	    		
	    		//Determines which parameter line was for
	    		switch(line[0])
	    		{
	    			//Name
	    			case "name":
	    				name = line[1];
	    				break;
	    				
		    		//Minimum Roughness
	    			case "minRoughness":
	    				minRoughness = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Maximum Roughness
	    			case "maxRoughness":
	    				maxRoughness = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Population Modifier
	    			case "popMod":
	    				popMod = Double.valueOf(line[1]);
	    				break;
	    				
	    			//Minimum Mineral Reserve
	    			case "minMinerals":
	    				minMinerals = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Maximum Mineral Reserve
	    			case "maxMinerals":
	    				maxMinerals = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Invalid Entry
	    			default:
	    		}
	    	}
	    	
	    	//If file is not ignore file, adds new climate
	    	if(!next.equalsIgnoreCase("#IGNORE"))
	    	{
	    		climates.add(new Climate(name, minRoughness, maxRoughness, popMod,
	    					minMinerals, maxMinerals));
	    	}
	    	
	    	sc.close();
	    }
		
		return climates;
	}
	
	//Generates a list of unit templates by reading through files and getting definitions
	public static ArrayList<LandUnitTemplate> generateLandTemplates(String corePath, Faction owner) throws FileNotFoundException
	{
		ArrayList<LandUnitTemplate> templates = new ArrayList<LandUnitTemplate>();

		//Establishes Climates directory and Scanner
		File folder = new File(corePath+"\\land_units"); 
		File files[] = folder.listFiles();
		Scanner sc; 
		String next = "";
		
		//Land Unit variables, before each file read set to default values
		String type;
		String transportType;
		int buildTime;
		int cost;
		int manpower;
		int alloys;
		int hp;
		int hardness;
		//int adaptability;
		int attack;
		int defense;
		int boardingAttack;
		int boardingDefense;
		int infantryPower;
		int armorPower;
		int artilleryPower;
		int airPower;
			  
		//Reads through all files in directory, generating land unit templates based on
		//definitions in files
	    for(File file : files) 
	    {
	    	//Default land unit template values
	    	type = "MISSING NAME";
			transportType = "none";	//NOTE - special case, indicates unit is made alongside ship
			buildTime = 1;
			cost = 0;
			manpower = 0;
			alloys = 0;
			hp = 1;
			hardness = 0;
			//adaptability = 0;
			attack = 0;
			defense = 0;
			boardingAttack = 0;
			boardingDefense = 0;
			infantryPower = 0;
			armorPower = 0;
			artilleryPower = 0;
			airPower = 0;
			
	    	sc = new Scanner(file);
	    	
	    	//Scans through file line by line
	    	while(sc.hasNextLine())
	    	{
	    		next = sc.nextLine();
	    		
	    		//Checks if file is an ignored file, if so, ends scan
	    		if(next.equalsIgnoreCase("#IGNORE")) { break; }
	    		
	    		//Splits line by '=', should split into two
	    		String[] line = next.split("=");
	    		
	    		//Determines which parameter line was for
	    		switch(line[0])
	    		{
	    			//Type
	    			case "type":
	    				type = line[1];
	    				break;
	    				
	    			//Transport Type
	    			case "transportType":
	    				transportType = line[1];
	    				break;

		    		//Build Time
	    			case "buildTime":
	    				buildTime = Integer.valueOf(line[1]);
	    				break;
	    				
		    		//Cost
	    			case "cost":
	    				cost = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Manpower Cost
	    			case "manpower":
	    				manpower = Integer.valueOf(line[1]);
	    				break;

	    			//Alloys Cost
	    			case "alloys":
	    				alloys = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Max Hitpoints
	    			case "hp":
	    				hp = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Hardness
	    			case "hardness":
	    				hardness = Integer.valueOf(line[1]);
	    				break;

	    			//Attack
	    			case "attack":
	    				attack = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Defense
	    			case "defense":
	    				defense = Integer.valueOf(line[1]);
	    				break;

	    			//Boarding Attack
	    			case "boardingAttack":
	    				boardingAttack = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Boarding Defense
	    			case "boardingDefense":
	    				boardingDefense = Integer.valueOf(line[1]);
	    				break;

	    			//Infantry Power
	    			case "infantryPower":
	    				infantryPower = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Armor Power
	    			case "armorPower":
	    				armorPower = Integer.valueOf(line[1]);
	    				break;

	    			//Artillery Power
	    			case "artilleryPower":
	    				artilleryPower = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Air Power
	    			case "airPower":
	    				airPower = Integer.valueOf(line[1]);
	    				break;
		    				
	    			//Invalid Entry
	    			default:
	    		}
	    	}
	    	
	    	//If file is not ignore file, adds new climate
	    	if(!next.equalsIgnoreCase("#IGNORE"))
	    	{
	    		templates.add(new LandUnitTemplate(owner, type, transportType,
						buildTime, cost, manpower, alloys,
						hp, hardness,
						attack, defense, boardingAttack, boardingDefense,
						infantryPower, armorPower, artilleryPower, airPower));
	    	}
	    	
	    	sc.close();
	    }
	    
		return templates;
	}

	//Generates a list of unit templates by reading through files and getting definitions
	public static ArrayList<NavalUnitTemplate> generateNavalTemplates(String corePath, Faction owner) throws FileNotFoundException
	{
		ArrayList<NavalUnitTemplate> templates = new ArrayList<NavalUnitTemplate>();

		//Establishes ships directory and Scanner
		File folder = new File(corePath+"\\naval_units"); 
		File nameFolder = new File(corePath+"\\names\\ship_names");
		File files[] = folder.listFiles();
		File nameFiles[] = nameFolder.listFiles();
		Scanner sc; 
		String next = "";
		
		//First gets all the ship names
		ArrayList<String> nameType = new ArrayList<String>();
		ArrayList<ArrayList<String>> namesArr = new ArrayList<ArrayList<String>>();
		for(File file : nameFiles)
		{
	    	sc = new Scanner(file);
	    	ArrayList<String> names = new ArrayList<String>();
	    	boolean isIgnore = false;

    		next = sc.nextLine();
    		
    		//Checks if file is an ignored file, if so, ends scan
    		if(next.equalsIgnoreCase("#IGNORE")) { isIgnore = true; }
    		
    		//Should be type, adds type to type's array to match up arrays
    		else
    		{
	    		//Splits line by '=', should split into two
	    		String[] line = next.split("=");
	    		nameType.add(line[1]);
    		}
    		
    		//Checks if not ignore file
    		if(!isIgnore)
    		{
		    	//Scans through file line by line
		    	while(sc.hasNextLine())
		    	{
		    		names.add(sc.nextLine());
		    		
		    	}

		    	//Adds list of names to names array
	    		namesArr.add(names);
    		}
	    	
	    	sc.close();
		}
		
		//Land Unit variables, before each file read set to default values
		String type;
		String cargoType;
		int buildTime;
		int cost;
		int manpower;
		int alloys;
		int hp;
		int size;
		int armor;
		int evasion;
		int firepower;
		int range;
		int craftHP;
		int craftPower;
		int craftDefense;
			  
		//Reads through all files in directory, generating land unit templates based on
		//definitions in files
	    for(File file : files) 
	    {
	    	//Default land unit template values
	    	type = "MISSING NAME";
			cargoType = "none";	//NOTE - special case, indicates unit is made alongside land unit
			buildTime = 1;
			cost = 0;
			manpower = 0;
			alloys = 0;
			hp = 1;
			size = 0;
			armor = 0;
			evasion = 0;
			firepower = 0;
			range = 0;
			craftHP = 0;
			craftPower = 0;
			craftDefense = 0;
			
	    	sc = new Scanner(file);
	    	
	    	//Scans through file line by line
	    	while(sc.hasNextLine())
	    	{
	    		next = sc.nextLine();
	    		
	    		//Checks if file is an ignored file, if so, ends scan
	    		if(next.equalsIgnoreCase("#IGNORE")) { break; }
	    		
	    		//Splits line by '=', should split into two
	    		String[] line = next.split("=");
	    		
	    		//Determines which parameter line was for
	    		switch(line[0])
	    		{
	    			//Type
	    			case "type":
	    				type = line[1];
	    				break;
	    				
	    			//Transport Type
	    			case "cargoType":
	    				cargoType = line[1];
	    				break;

		    		//Build Time
	    			case "buildTime":
	    				buildTime = Integer.valueOf(line[1]);
	    				break;
		    				
		    		//Cost
	    			case "cost":
	    				cost = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Manpower Cost
	    			case "manpower":
	    				manpower = Integer.valueOf(line[1]);
	    				break;

	    			//Alloys Cost
	    			case "alloys":
	    				alloys = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Max Hitpoints
	    			case "hp":
	    				hp = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Size
	    			case "size":
	    				size = Integer.valueOf(line[1]);
	    				break;

	    			//Armor
	    			case "armor":
	    				armor = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Evasion
	    			case "evasion":
	    				evasion = Integer.valueOf(line[1]);
	    				break;

	    			//Firepower
	    			case "firepower":
	    				firepower = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Range
	    			case "range":
	    				range = Integer.valueOf(line[1]);
	    				break;

	    			//Craft HP
	    			case "craftHP":
	    				craftHP = Integer.valueOf(line[1]);
	    				break;
	    				
	    			//Craft Attack
	    			case "craftPower":
	    				craftPower = Integer.valueOf(line[1]);
	    				break;

	    			//Craft Defense
	    			case "craftDefense":
	    				craftDefense = Integer.valueOf(line[1]);
	    				break;
		    				
	    			//Invalid Entry
	    			default:
	    		}
	    	}
	    	
	    	//If file is not ignore file, adds new climate
	    	if(!next.equalsIgnoreCase("#IGNORE"))
	    	{
	    		ArrayList<String> names = new ArrayList<String>();
	    		
	    		//Gets list of names
	    		int idx=0;
	    		for(idx=0; idx < nameType.size(); idx++)
	    		{
	    			//If types match up, current idx should give correct names in namesArr
	    			if(nameType.get(idx).equals(type))
	    			{
	    				names = namesArr.get(idx);
	    				break;
	    			}
	    		}
	    		
	    		//Adds new template
	    		templates.add(new NavalUnitTemplate(owner, type, cargoType,
						buildTime, cost, manpower, alloys,
						hp, size, armor, evasion,
						firepower, range, craftHP, craftPower, craftDefense,
						names));
	    	}
	    	
	    	sc.close();
	    }
	    
		return templates;
	}
	
	//Calculates starting cost of a planet
	//Returns that cost value
	public static int costPlanet(Planet planet)
	{
		int cost=0;
		
		cost+=Math.ceil(planet.getPopulation())*100;
		cost+=planet.getMineralReserves()*25;
		
		return cost;
	}
	
	//Using point cost of planet, determines an appropriate garrison army
	//Returns list of garrison units
	public static ArrayList<LandUnit> fillArmy(int cost, Faction neutral)
	{
		ArrayList<LandUnit> units = new ArrayList<LandUnit>();
		int spent = cost;
		
		//Poor planet, places only Militia
		if(cost <= 500)
		{
			while(spent > 0)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Militia Corps")));
				spent -= 100;
			}
		}
		//Below Average Planet, places two Militia and rest Infantry
		else if(cost <= 1000)
		{
			units.add(new LandUnit(neutral.getLandTemplate("Militia Corps")));
			units.add(new LandUnit(neutral.getLandTemplate("Militia Corps")));
			spent -= 200;
			while(spent > 0)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Infantry Corps")));
				spent -= 200;
			}
		}
		//Average Planet, places 75% Infantry 25% Armor
		else if(cost <= 2000)
		{
			while(spent > (cost*6)/10)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Armor Corps")));
				spent -= 400;
			}
			while(spent > 0)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Infantry Corps")));
				spent -= 200;
			}
		}
		//Above Average Planet, places 66% Infantry 33% Armor
		else if(cost <= 3200)
		{
			while(spent > (cost*5)/10)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Armor Corps")));
				spent -= 400;
			}
			while(spent > 0)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Infantry Corps")));
				spent -= 200;
			}
		}
		//Rich Planet, places 50% Infantry 50% Armor
		else
		{
			while(spent > (cost*38)/100)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Armor Corps")));
				spent -= 400;
			}
			while(spent > 0)
			{
				units.add(new LandUnit(neutral.getLandTemplate("Infantry Corps")));
				spent -= 200;
			}
		}
		
		return units;
	}
	
	//Using point cost of planet, determines an appropriate garrison fleet
	//Returns list of garrison ships
	public static ArrayList<NavalUnit> fillFleet(int cost, Faction neutral)
	{
		ArrayList<NavalUnit> ships = new ArrayList<NavalUnit>();
		int spent = cost;

		//Poor planet, places only Frigates
		if(cost <= 600)
		{
			while(spent > 0)
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Frigate")));
				spent -= 100;
			}
		}
		//Below Average Planet, 50% LC's 50% Frigates
		else if(cost <= 1200)
		{
			while(spent > (cost*33)/100)
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Light Cruiser")));
				spent -= 200;
			}
			while(spent > 0)
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Frigate")));
				spent -= 100;
			}
		}
		//Average Planet, places 20% HC's, 40% LC's, 40% Frigates
		else if(cost <= 2400)
		{
			while(spent > (cost*83)/100)	//2
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Heavy Cruiser")));
				spent -= 400;
			}
			while(spent > (cost*17)/100)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Light Cruiser")));
				spent -= 400;
			}
			while(spent > 0)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Frigate")));
				spent -= 100;
			}
		}
		//Above Average Planet, places 9% BC's, 18% HC's, 36% LC's, 36% Frigates
		else if(cost <= 3200)
		{
			while(spent > (cost*75)/100)	//1
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Battlecruiser")));
				spent -= 800;
			}
			while(spent > (cost*63)/100)	//2
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Heavy Cruiser")));
				spent -= 400;
			}
			while(spent > (cost*13)/100)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Light Cruiser")));
				spent -= 400;
			}
			while(spent > 0)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Frigate")));
				spent -= 100;
			}
		}
		//Rich Planet, places 8% Carriers, 8% BC's, 17% HC's, 33% LC's, 33% Frigates
		else
		{
			while(spent > (cost*8)/10)	//1
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Carrier")));
				spent -= 800;
			}
			while(spent > (cost*6)/10)	//1
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Battlecruiser")));
				spent -= 800;
			}
			while(spent > (cost*5)/10)	//2
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Heavy Cruiser")));
				spent -= 400;
			}
			while(spent > (cost*1)/10)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Light Cruiser")));
				spent -= 400;
			}
			while(spent > 0)	//4
			{
				ships.add(new NavalUnit(neutral.getNavalTemplate("Frigate")));
				spent -= 100;
			}
		}
		
		return ships;
	}
	
	//Army ID getter and incrementer
	public static long getAndIncArmyID() 
	{ 
		long temp = armyCounter;  
		armyCounter++; 
		return temp; 
	}

	//Unit ID getter and incrementer
	public static long getAndIncUnitID() 
	{ 
		long temp = unitCounter;  
		unitCounter++; 
		return temp; 
	}

	//Fleet ID getter and incrementer
	public static long getAndIncFleetID() 
	{ 
		long temp = fleetCounter;  
		fleetCounter++; 
		return temp; 
	}

	//Ship ID getter and incrementer
	public static long getAndIncShipID() 
	{ 
		long temp = shipCounter;  
		shipCounter++; 
		return temp; 
	}
}
