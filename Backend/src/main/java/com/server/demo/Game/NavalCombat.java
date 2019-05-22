package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class NavalCombat {

	private Planet planet;
	private Hyperlane lane;
	@JsonBackReference
	private ArrayList<Faction> combatants;
	@JsonBackReference
	private ArrayList<ArrayList<Fleet>> fleets;
	private int phase;
	private int day;
	
	//Constructor - Orbital engagement
	public NavalCombat(Planet planet, Fleet attacker)
	{
		this.planet = planet;
		this.lane = null;
		this.phase = 0;
		this.day = 0;
		
		this.combatants = new ArrayList<Faction>();
		if(planet!=null) { this.combatants.add(planet.getFleet(0).getOwner()); }
		this.combatants.add(attacker.getOwner());
		
		this.fleets = new ArrayList<ArrayList<Fleet>>();
		if(planet!=null) 
		{ 
			ArrayList<Fleet> fleetGroupD = new ArrayList<Fleet>();
			for(Fleet fleet : planet.getFleets()) { fleetGroupD.add(fleet); }
			this.fleets.add(fleetGroupD); 
		}
		
		ArrayList<Fleet> fleetGroupA = new ArrayList<Fleet>();
		fleetGroupA.add(attacker);
		this.fleets.add(fleetGroupA);
	}
	
	//Constructor - Mid-space engagement
	public NavalCombat(Hyperlane lane, Fleet attacker)
	{
		this((Planet) null, attacker);
		this.lane = lane;
		
		this.combatants.add(lane.getFleet(0).getOwner());
		
		ArrayList<Fleet> fleetGroupD = new ArrayList<Fleet>();
		for(Fleet fleet : lane.getFleets()) { fleetGroupD.add(fleet); }
		this.fleets.add(fleetGroupD); 
	}
	
	//Updates state of battle
	public void update()
	{
		day++;

		if(!complete()) { combat(); }
		
		//Cleans up dead fleets
		for(int i = 0; i < fleets.size(); i++)
		{
			ArrayList<Fleet> fleetGroup = fleets.get(i);
			
			Faction combatant = fleetGroup.get(0).getOwner();
			
			for(int j = 0; j < fleetGroup.size(); j++)
			{
				Fleet fleet = fleetGroup.get(j);
				
				//Removes army from group if no units left, disbands army
				if(fleet.getShips().size() == 0)
				{
					fleet.getCargo().getUnits().clear();
					fleet.disband();
					fleetGroup.remove(j);
				}
			}
			
			//Removes army group and owner combatant if no armies
			if(fleetGroup.size() == 0)
			{
				combatants.remove(combatantIdx(combatant));
				fleets.remove(i);
			}
		}

		phase = phase < 5 ? phase+1 : 5;
	}

	//Adds fleet to battle
	public void addFleet(Fleet fleet)
	{
		int idx = combatantIdx(fleet.getOwner());
		
		//Non-combatant faction joins the battle
		if(idx == -1)
		{
			combatants.add(fleet.getOwner());
			ArrayList<Fleet> fleetGroup = new ArrayList<Fleet>();
			fleetGroup.add(fleet);
			fleets.add(fleetGroup);
		}
		else
		{
			if(!fleets.get(idx).contains(fleet))
			{
				fleets.get(idx).add(fleet);
			}
		}
	}
	
	public Planet getPlanet() { return planet; }
	public Hyperlane getLane() { return lane; }
	public ArrayList<Faction> combatants() { return combatants; }
	public ArrayList<ArrayList<Fleet>> fleets() { return fleets; }
	public int day() { return day; }
	public int phase() { return phase; }
	public Game game() { return planet != null ? planet.game() : lane.game(); }
	public boolean complete() { return fleets.size() <= 1; }

	//Returns the combatant idx of the given faction
	//Returns -1 if faction is not present in battle
	private int combatantIdx(Faction faction)
	{
		for(int i = 0; i < combatants.size(); i++)
		{
			if(combatants.get(i).equals(faction)) { return i; }
		}
		
		return -1;
	}

	//Outer Combat Algorithm
	private void combat()
	{
		Random rng = new Random();
		
		ArrayList<Integer> toFight = new ArrayList<Integer>();
		for(int enIdx = 0; enIdx < combatants.size(); enIdx++) { toFight.add(enIdx); }
		
		//Each combatant gets engagement phase with every other combatant
		//where combatant i attacks i+1 -> n
		while(toFight.size() > 0)
		{
			int fightIdx = rng.nextInt(toFight.size());
			int enIdx = toFight.get(fightIdx);
			
			for(int j = enIdx+1; j < combatants.size(); j++)
			{
				engage(enIdx, j);
			}
			
			toFight.remove(fightIdx);
		}
	}

	//Engagement Algorithm
	private void engage(int idxA, int idxB)
	{
		ArrayList<NavalUnit> shipsA = new ArrayList<NavalUnit>();
		for(Fleet fleet : fleets.get(idxA)) { shipsA.addAll(fleet.getShips()); }
		
		ArrayList<NavalUnit> shipsB = new ArrayList<NavalUnit>();
		for(Fleet fleet : fleets.get(idxB)) { shipsB.addAll(fleet.getShips()); }
		
		engageLoop(shipsA, shipsB);
		engageLoop(shipsB, shipsA);
	}

	//Engagement loop
	private void engageLoop(ArrayList<NavalUnit> givers, ArrayList<NavalUnit> takers)
	{
		Random rng = new Random();
		for(int idx = 0; idx < givers.size() && givers.size() > 0 && takers.size() > 0; idx++)
		{
			NavalUnit ship = givers.get(idx);
			NavalUnit target = takers.get(rng.nextInt(takers.size()));

			if(ship.getFleet() != null && target.getFleet() != null)
			{
				int damageA = damage(ship, target);
				int damageCraftA = 0;
				int damageCraftB = 0;
				int damageBoardA = 0;
				int damageBoardB = 0;
				
				if(phase > 1 && ship.getCraftHP() > 0)
				{
					damageCraftA = damageCraftAttack(ship, target);
					damageCraftB = damageCraftDefend(target, ship);
				}
				//Boarding takes place starting with phase 4
				//When cargo is under 25% HP, no attempt to board
				if(phase > 3 && ship.getCargo().getHP() > (ship.getCargo().getMaxHP()/4))
				{
					damageBoardA = damageBoard(ship, target, true);
					damageBoardB = damageBoard(target, ship, false);
					
					//If boarders will drop to below 20% HP and will not strike a fatal blow,
					//no boarding attempted
					if( (ship.getCargo().getHP() - damageBoardB < ship.getCargo().getMaxHP()/5)
					 && (target.getCargo().getHP() - damageBoardA > 0))
					{
						damageBoardA = 0;
						damageBoardB = 0;
					}
				}
				
				//TODO - remove
				/*
				System.out.println(ship.getFleet().getOwner().getPlayer()+"'s "+ship.getType()
				+" ("+ship.getHP()+") does "+damageA+" damage to "+target.getType()+" ("+target.getHP()+")");
				
				System.out.println(ship.getFleet().getOwner().getPlayer()+"'s "+ship.getType()
				+"'s Crafts ("+ship.getCraftHP()+") do "+damageCraftA+" Craft damage to "+target.getType()+" ("+target.getHP()+")");

				System.out.println(ship.getFleet().getOwner().getPlayer()+"'s "+ship.getCargo().getType()
				+" ("+ship.getCargo().getHP()+") does "+damageBoardA+" boarding damage to "+target.getCargo().getType()+" ("+target.getCargo().getHP()+")");

				System.out.println(target.getFleet().getOwner().getPlayer()+"'s "+target.getType()
				+" ("+target.getHP()+") does "+damageCraftB+" damage to "+ship.getType()+"'s Crafts ("+ship.getCraftHP()+")");

				System.out.println(target.getFleet().getOwner().getPlayer()+"'s "+target.getCargo().getType()
				+" ("+target.getCargo().getHP()+") does "+damageBoardB+" boarding damage to "+ship.getCargo().getType()+" ("+ship.getCargo().getHP()+")");
				
				System.out.println();
				*/
				int hpCraftA = ship.getCraftHP() - damageCraftB > 0 ? ship.getCraftHP() - damageCraftB : 0;
				if(ship.getCraftHP() != hpCraftA) { ship.setCraftHP(hpCraftA); }
				
				//Shouldn't take more than 50% damage
				int hpCargoA = ship.getCargo().getHP() - damageBoardB > 0 ? ship.getCargo().getHP() - damageBoardB : 0;
				if(ship.getCargo().getHP() != hpCargoA) { ship.getCargo().setHP(hpCargoA); }
				
				int hpCargoB = target.getCargo().getHP() - damageBoardA > 0 ? target.getCargo().getHP() - damageBoardA : 0;
				if(target.getCargo().getHP() != hpCargoB) { target.getCargo().setHP(hpCargoB); }
				
				int hpB = target.getHP() - damageA - damageCraftA;
				if(hpB <= 0 || hpCargoB <= 0) { target.setHP(0); target.disband(); takers.remove(target); }
				else if(target.getHP() != hpB) { target.setHP(hpB); }
	
			}
			else if(ship.getFleet() == null) { givers.remove(ship); }
			else { takers.remove(target); }
		}
	}
	
	//Damage Algorithm - Ship Guns
	private int damage(NavalUnit giver, NavalUnit taker)
	{
		Random rng = new Random();
		
		if(giver.getRange()+phase < 5) { return 0; }	//Out of range
		
		int hitPercent = (100 - rng.nextInt((taker.getEvasion()+1)*5))*(taker.getSize()*10)/100;
		hitPercent = hitPercent == 0 ? 1 : hitPercent;
		int guard = taker.getArmor()-giver.getFirepower();
		
		int div = 1000;
		return (giver.getHP()*100/giver.getMaxHP()) * (giver.getFirepower()*2) * hitPercent * (100 - guard*10)/div;
	}
	
	//Damage Algorithm - Craft Attack (Damage to ship)
	private int damageCraftAttack(NavalUnit giver, NavalUnit taker)
	{
		//Craft has 5 pierce
		int craftPierce = 5;
		int guard = (taker.getArmor() - craftPierce) > 0 ? taker.getArmor() - craftPierce : 0;
		
		int div = 100;
		return (giver.getCraftHP() * giver.getCraftPower() * (10-guard))/div;
	}

	//Damage Algorithm - Craft Defense (Damage to craft)
	private int damageCraftDefend(NavalUnit giver, NavalUnit taker)
	{
		return ((giver.getHP()*100/giver.getMaxHP()) * giver.getCraftDefense());
	}
	
	//Damage Algorithm - Boarding Damage (damage to cargo)
	private int damageBoard(NavalUnit giver, NavalUnit taker, boolean isDef)
	{
		LandUnit cargo = giver.getCargo();
		int base = isDef ? cargo.getBoardingDefense() : cargo.getBoardingAttack();
		
		return (cargo.getHP() * base) / 40;
	}
}
