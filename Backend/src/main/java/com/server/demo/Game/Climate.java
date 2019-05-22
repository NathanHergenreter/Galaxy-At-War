package com.server.demo.Game;

//Template for planet stats
public class Climate {
	
	private String name;
	private int minRoughness;	//From 0-100
	private int maxRoughness;	//From 0-100
	private double popMod;	//From 0.01-1.0
	private int minMinerals;	//From 0-9
	private int maxMinerals;	//From 0-9
	
	//Constructor
	//Takes in all of it data member values
	//All definitions are made in the txt file
	public Climate(String name, int minRoughness, int maxRoughness, double popMod,
					int minMinerals, int maxMinerals)
	{
		this.name = name;
		this.minRoughness = minRoughness;
		this.maxRoughness = maxRoughness;
		this.popMod = popMod;
		this.minMinerals = minMinerals;
		this.maxMinerals = maxMinerals;
	}
	
	//Name
	public String getName() { return name; }
	
	//Roughness Range
	public int getMinRoughness() { return minRoughness; }
	public int getMaxRoughness() { return maxRoughness; }
	
	//Pop Mod
	public double getPopMod() { return popMod; }
	
	//Mineral Range
	public int getMinMinerals() { return minMinerals; }
	public int getMaxMinerals() { return maxMinerals; }
}
