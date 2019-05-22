package net.javatutorials.tutorials;

import java.util.ArrayList;

public class MapGenerator {
	public static ArrayList<Planet> generate(int num)
	{
		ArrayList<Planet> planets = new ArrayList<Planet>();
		for(int i=0; i<num; i++)
		{
			planets.add(new Planet(i));
		}
		return planets;
	}
}
