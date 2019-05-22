package com.server.demo.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class LandCombat {

	@JsonBackReference
	private Planet location;
	@JsonBackReference
	private ArrayList<Faction> combatants;
	@JsonBackReference
	private ArrayList<ArrayList<Army>> armies;
	private int day;
	
	private static final int adaptInf = 100;
	private static final int adaptArm = 20;
	private static final int adaptArt = 50;
	private static final int adaptAir = 70;
	
	private static final int pierceInf = 3;
	private static final int pierceArm = 6;
	private static final int pierceArt = 8;
	private static final int pierceAir = 7;
	
	//Constructor
	public LandCombat(Planet location, Army attacker)
	{
		this.location = location;
		this.day = 0;
		
		this.combatants = new ArrayList<Faction>();
		this.combatants.add(location.getOwner());
		this.combatants.add(attacker.getOwner());
		
		this.armies = new ArrayList<ArrayList<Army>>();

		ArrayList<Army> armyGroupD = new ArrayList<Army>();
		for(Army army : location.getArmies()) { armyGroupD.add(army); }
		this.armies.add(armyGroupD);
		
		ArrayList<Army> armyGroupA = new ArrayList<Army>();
		armyGroupA.add(attacker);
		this.armies.add(armyGroupA);
	}
	
	//Updates state of battle
	public void update()
	{
		day++;
		
		if(!complete()) { combat(); }
		
		//Cleans up dead armies
		for(int i = 0; i < armies.size(); i++)
		{
			ArrayList<Army> armyGroup = armies.get(i);
			Faction combatant = armyGroup.get(0).getOwner();
			for(int j = 0; j < armyGroup.size(); j++)
			{
				Army army = armyGroup.get(j);
				
				//Removes army from group if no units left, disbands army
				if(army.getUnits().size() == 0)
				{
					army.getTransport().getShips().clear();
					army.disband();
					armyGroup.remove(j);
				}
			}
			
			//Removes army group and owner combatant if no armies
			if(armyGroup.size() == 0)
			{
				
				combatants.remove(combatantIdx(combatant));
				armies.remove(i);
			}
		}
	}
	
	/*
	 * Getters and Setters
	 * 
	 */

	public Game game() { return location.game(); }
	
	//Adds army to battle
	public void addArmy(Army army)
	{
		int idx = combatantIdx(army.getOwner());
		
		//Non-combatant faction joins the battle
		if(idx == -1)
		{
			combatants.add(army.getOwner());
			ArrayList<Army> armyGroup = new ArrayList<Army>();
			armyGroup.add(army);
			armies.add(armyGroup);
		}
		else
		{
			if(!armies.get(idx).contains(army))
			{
				armies.get(idx).add(army);
			}
		}
	}
	
	public Planet getLocation() { return location; }
	public ArrayList<Faction> combatants() { return combatants; }
	public Faction defender() { return location.getOwner().equals(combatants.get(0)) ? location.getOwner() : null; }
	public ArrayList<ArrayList<Army>> armies() { return armies; }
	public int day() { return day; }
	public boolean complete() { return armies.size() <= 1; }

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
		ArrayList<LandUnit> unitsA = new ArrayList<LandUnit>();
		for(Army army : armies.get(idxA)) { unitsA.addAll(army.getUnits()); }
		
		ArrayList<LandUnit> unitsB = new ArrayList<LandUnit>();
		for(Army army : armies.get(idxB)) { unitsB.addAll(army.getUnits()); }
		
		engageLoop(unitsA, unitsB);
		engageLoop(unitsB, unitsA);
	}
	
	//Engagement loop
	private void engageLoop(ArrayList<LandUnit> givers, ArrayList<LandUnit> takers)
	{
		Random rng = new Random();
		for(int idx = 0; idx < givers.size() && givers.size() > 0 && takers.size() > 0; idx++)
		{
			LandUnit unit = givers.get(idx);
			LandUnit target = takers.get(rng.nextInt(takers.size()));
			
			if(unit.getArmy() != null && target.getArmy() != null)
			{
				int damageA = damage(unit, target);
				int damageB = damage(target, unit);
				
				//TODO - remove
				/*
				System.out.println(unit.getArmy().getOwner().getPlayer()+"'s "+unit.getType()
				+" ("+unit.getHP()+") does "+damageA+" damage to "+target.getType()+" ("+target.getHP()+")");
				System.out.println(target.getArmy().getOwner().getPlayer()+"'s "+target.getType()
				+" ("+target.getHP()+") does "+damageB+" damage to "+unit.getType()+" ("+unit.getHP()+")");
				System.out.println();
				*/
				
				int hpA = unit.getHP() - damageB;
				if(hpA <= 0) { unit.setHP(0); unit.disband(); idx--; givers.remove(unit); }
				else if(unit.getHP() != hpA) { unit.setHP(hpA); }
	
				int hpB = target.getHP() - damageA;
				if(hpB <= 0) { target.setHP(0); target.disband(); takers.remove(target); }
				else if(target.getHP() != hpB) { target.setHP(hpB); }
			}
			else if(unit.getArmy() == null) { givers.remove(unit); }
			else { takers.remove(target); }
		}
	}
	
	//Damage Algorithm
	private int damage(LandUnit giver, LandUnit taker)
	{
		boolean isDef = giver.getArmy().getOwner().equals(defender());
		
		Random rng = new Random();
		int hardness = taker.getHardness();
		int roughness = location.getRoughness();
		
		//Effectiveness of taker hardness
		int infGuard = (hardness - pierceInf)/2 > 0 ? (hardness - pierceInf)/2 : 0;
		int armGuard = (hardness - pierceArm)/2 > 0 ? (hardness - pierceArm)/2 : 0;
		int artGuard = (hardness - pierceArt)/2 > 0 ? (hardness - pierceArt)/2 : 0;
		int airGuard = (hardness - pierceAir)/2 > 0 ? (hardness - pierceAir)/2 : 0;

		//Terrain penalty
		int infTPen = 100 - ((100-adaptInf)*roughness)/100 > 0 ? 100 - ((100-adaptInf)*roughness)/100 : 1;
		int armTPen = 100 - ((100-adaptArm)*roughness)/100 > 0 ? 100 - ((100-adaptArm)*roughness)/100 : 1;
		int artTPen = 100 - ((100-adaptArt)*roughness)/100 > 0 ? 100 - ((100-adaptArt)*roughness)/100 : 1;
		int airTPen = 100 - ((100-adaptAir)*roughness)/100 > 0 ? 100 - ((100-adaptAir)*roughness)/100 : 1;
		
		int infDam = ((giver.getHP()*100/giver.getMaxHP())
					* (giver.getInfantryPower() - infGuard) //0-9
					* infTPen);
		
		int armDam = ((giver.getHP()*100/giver.getMaxHP())
				* (giver.getArmorPower() - armGuard) //0-9
				* armTPen);
		
		int artDam = ((giver.getHP()*100/giver.getMaxHP())
				* (giver.getArtilleryPower() - artGuard) //0-9
				* artTPen);
				
		int airDam = ((giver.getHP()*100/giver.getMaxHP()) 
				* (giver.getAirPower() - airGuard) //0-9
				* airTPen);

		int base = isDef ? giver.getDefense() : giver.getAttack();
		int div = 100;
		int spread = combatants.size()-1;	//Damage is "spread" based on how many combatants
		
		return (base * (infDam + armDam + artDam + airDam) / div) / spread;
	}
}
