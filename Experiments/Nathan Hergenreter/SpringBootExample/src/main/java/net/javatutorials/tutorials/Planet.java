package net.javatutorials.tutorials;

public class Planet {
	int id;
	int[] coords = new int[2];

	public Planet(int id)
	{
		this.id=id;
		coords[0]=0;
		coords[1]=0;
	}
	
	public int id()
	{
		return id;
	}
	
	public int x()
	{
		return coords[0];
	}
	
	public int y()
	{
		return coords[1];
	}
	
	public String toString()
	{
		return "ID: "+id+" ;; Coords: ("+x()+", "+y()+") ";
	}
}
