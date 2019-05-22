package com.server.demo.Game;

import java.util.ArrayList;

public class GMTTools {

	Game game;
	
	public GMTTools(Game game)
	{
		this.game = game;
	}
	
	/*
	 * (B)
	 * Method Extension Helpers
	 */

	//Determines Planets on shortest path to destination
	public ArrayList<Planet> fleetMovePath(Fleet fleet, Planet target)
	{
		ArrayList<Planet> path = new ArrayList<Planet>();
		int n = game.getPlanets().size();
		ArrayList<Planet> planets = game.getPlanets();
		
		Planet origin;
		if(fleet.getStation()!=null) { origin = fleet.getStation(); }
		else
		{
			Planet dest = fleet.getMoveQueue().get(0);
			Hyperlane curLane = fleet.getLane();
			origin = curLane.getA().equals(dest) ? curLane.getB() : curLane.getA();
		}
		int dist[] = shortestPath(n, origin);

		ArrayList<Planet> pathMirror = new ArrayList<Planet>();
		for(int curIdx = (int) target.getID(); curIdx != (int) origin.getID();)
		{
			Planet cur = planets.get(curIdx);
			ArrayList<Hyperlane> lanes = cur.getHyperlanes();

			pathMirror.add(cur);
			
			int minIdx = -1;
			for(Hyperlane e : lanes)
			{
				Planet v = e.getA().equals(cur) ? e.getB() : e.getA();
				int vIdx = (int) v.getID();
				
				if(!pathMirror.contains(v) && !v.equals(origin)) { minIdx = vIdx; }
			}
			
			for(Hyperlane e : lanes)
			{
				Planet v = e.getA().equals(cur) ? e.getB() : e.getA();
				
				int vIdx = (int) v.getID();
				
				//If target planet, sets that as minIdx
				if(v.equals(origin)) { minIdx = vIdx; break; }
				
				//Otherwise, looks for planet on shortest path
				if((dist[vIdx] < dist[minIdx]) && !pathMirror.contains(v) && !v.equals(origin)) { minIdx = vIdx; }
			}

			curIdx = minIdx;
		}
		
		for(int i = pathMirror.size()-1; i>=0; i--) { path.add(pathMirror.get(i)); }
		
		return path;
	}
	
	//Dijkstra's Shortest Path
	public int[] shortestPath(int n, Planet origin)
	{
		int dist[] = new int[n];
		boolean done[] = new boolean[n];
		for(int i = 0; i < n; i++)
		{
			dist[i] = Integer.MAX_VALUE;
			done[i] = false;
		}
		
		dist[(int) origin.getID()] = 0;
		
		// Find shortest path for all vertices 
        for (int count = 0; count < n-1; count++) 
        { 
            Planet u = minDistance(dist, done);
            int uIdx = (int) u.getID();
  
            done[uIdx] = true;
  
            // Update dist value of the adjacent vertices of the 
            // picked vertex. 
            for (Hyperlane e : u.getHyperlanes()) 
            {
            	int eDist = (int) e.getDistance();
            	Planet v = e.getA().equals(u) ? e.getB() : e.getA();
            	int vIdx = (int) v.getID();
            	
                if (!done[vIdx] 
                && (dist[uIdx] + eDist) < dist[vIdx] )
                {
                    dist[vIdx] = dist[uIdx] + eDist; 
                }
            }
        }
        
        return dist;
	}
	
	//Helper for Dijkstra's
	public Planet minDistance(int dist[], boolean done[]) 
    { 
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
  
        for (int v = 0; v < dist.length; v++) 
            if (!done[v] && dist[v] <= min)
            { 
                min = dist[v];
                minIndex = v;
            } 
  
        return game.getPlanets().get(minIndex); 
    } 
	
	/*
	 * (C)
	 * Search Functions
	 */

	//Helper Function - Returns faction with given id
	public Faction findFactionByID(long factionID)
	{
		ArrayList<Faction> factions = game.getFactions();
		
		for(Faction faction : factions)
		{
			//Finds the appropriate player faction
			if(faction.getID() == factionID) { return faction; }
		}
		
		return null;
	}
	
	//Helper Function - Returns a planet with given id
	public Planet findPlanetByID(long planetID)
	{
		ArrayList<Planet> planets = game.getPlanets();
		
		for(Planet planet : planets)
		{
			//Finds the appropriate player faction
			if(planet.getID() == planetID) { return planet; }
		}
		
		return null;
	}
	
	//Helper Function - Returns an army with the given id owned by given faction
	public Army findArmyByID(long armyID, Faction owner)
	{
		ArrayList<Army> armies = owner.getArmies();
		
		for(Army army : armies) 
		{ 
			//Finds the appropriate army
			if(army.getID() == armyID) { return army; }
		}
		
		return null;
	}

	//Helper Function - Returns a fleet with the given id owned by given faction
	public Fleet findFleetByID(long fleetID, Faction owner)
	{
		ArrayList<Fleet> fleets = owner.getFleets();
		
		for(Fleet fleet : fleets) 
		{ 
			//Finds the appropriate fleet
			if(fleet.getID() == fleetID) { return fleet; }
		}
		
		return null;
	}

	//Helper Function - Returns a unit template with the given id owned by given faction
	public LandUnitTemplate findUnitTypeByID(long typeID, Faction owner)
	{
		ArrayList<LandUnitTemplate> types = owner.getLandTemplates();
		
		for(LandUnitTemplate type : types) 
		{ 
			//Finds the appropriate unit template
			if(type.getID() == typeID) { return type; }
		}
		
		//Returns default unit if not found
		return types.get(0);
	}


	//Helper Function - Returns a ship template with the given id owned by given faction
	public NavalUnitTemplate findShipTypeByID(long typeID, Faction owner)
	{
		ArrayList<NavalUnitTemplate> types = owner.getNavalTemplates();
		
		for(NavalUnitTemplate type : types) 
		{ 
			//Finds the appropriate ship template
			if(type.getID() == typeID) { return type; }
		}
		
		//Returns default ship if not found
		return types.get(0);
	}
	
	//Helper Function - Returns a unit with the given id within the given army
	public LandUnit findUnitByID(long unitID, Army army)
	{
		for(LandUnit unit : army.getUnits())
		{
			if(unit.getID() == unitID) { return unit; }
		}
		
		return null;
	}
	
	//Helper Function - Returns a ship with the given id within the given fleet
	public NavalUnit findShipByID(long shipID, Fleet fleet)
	{
		for(NavalUnit ship : fleet.getShips())
		{
			if(ship.getID() == shipID) { return ship; }
		}
		
		return null;
	}
}
