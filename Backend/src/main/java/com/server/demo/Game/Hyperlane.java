package com.server.demo.Game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Arrays;

/*Class for edge between two planets
 * Contains:
 * 	- the two planet points it is an edge between
 * 	- a list of fleets  on the lane
 * 	- the lane's distance
 * 	- a base speed which determines how far a fleet moves on this lane,
 * 		in turn determining how long it takes to travel along the lane
 */
public class Hyperlane {

	@JsonManagedReference
	private Planet ptA, ptB;
	@JsonBackReference
	private ArrayList<Fleet> fleets = new ArrayList<Fleet>();
	private double distance;
	private int speed;
	@JsonBackReference
	private NavalCombat navalCombat;
	
	//Constructor
	//Takes the planets it is a lane between, distance and speed
	public Hyperlane(Planet ptA, Planet ptB, double distance, int speed)
	{
		this.ptA = ptA;
		this.ptB = ptB;
		this.distance = distance;
		this.speed = speed;
	}
	
	//Planets
	public Planet getA() { return ptA; }
	public Planet getB() { return ptB; }
	
	//Returns neighboring planet along this hyperlane
	public Planet getNeighbor(Planet start) 
	{ 
		if(start==ptA) { return ptB; }
		else { return ptA; }
	}
	
	//Update method, gives resources to owner and decrements/completes queue'd builds
	public void update()
	{
		if(this.navalCombat!=null)
		{
			this.navalCombat.update();
			if(navalCombat.complete())
			{
				this.navalCombat = null;

				//PtA id, PtB id
				ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();
				ids.add(new ArrayList<Long>(Arrays.asList(new Long(ptA.getID()))));
				ids.add(new ArrayList<Long>(Arrays.asList(new Long(ptB.getID()))));
				game().outputMessage(new UpdateMessage("combatEndNavalLane", ids));
			}
		}
	}

	public Game game() { return ptA.game(); }
	
	//Distance
	public double getDistance() { return distance; }
	
	//Speed
	public int getSpeed() { return speed; }

	//Fleets
	public ArrayList<Fleet> getFleets() { return fleets; }
	public Fleet getFleet(int idx) { return fleets.get(idx); }
	public void addFleet(Fleet fleet) { fleets.add(fleet); }
	public void removeFleet(Fleet fleet) { fleets.remove(fleet); }

	//Naval Combat
	public NavalCombat getNavalCombat() { return navalCombat; }
	public boolean hasNavalCombat() { return navalCombat!=null; }
	public void beginNavalCombat(Fleet attacker) 
	{ 
		navalCombat = new NavalCombat(this, attacker); 

		//PtA id, PtB id
		ArrayList<ArrayList<Long>> ids = new ArrayList<ArrayList<Long>>();;
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ptA.getID()))));
		ids.add(new ArrayList<Long>(Arrays.asList(new Long(ptB.getID()))));
		game().outputMessage(new UpdateMessage("combatStartNavalLane", ids));
	}
}
