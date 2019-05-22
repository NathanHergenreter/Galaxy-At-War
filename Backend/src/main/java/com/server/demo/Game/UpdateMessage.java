package com.server.demo.Game;

import java.util.ArrayList;

public class UpdateMessage {

		private String type;	//Commands
		private ArrayList<ArrayList<Long>> ids;	//2D array of id's
													//Must be 2D because some arguments are lists of id's
		int value;	//When a value is being updated, uses this for argument
		ArrayList<String> sValues;	//When a name/other string value is needed

		//Generic, no params other than id's
		public UpdateMessage(String type, ArrayList<ArrayList<Long>> ids)
		{
			this.type = type;
			this.ids = ids;
			this.value = 0;
			this.sValues = new ArrayList<String>();
		}
		
		//Takes an int param
		public UpdateMessage(String type, ArrayList<ArrayList<Long>> ids, int value)
		{
			this(type, ids);
			this.value = value;
		}
		
		//Takes a string param
		public UpdateMessage(String type, ArrayList<ArrayList<Long>> ids, ArrayList<String> sValues)
		{
			this(type, ids);
			this.sValues = sValues;
		}
		
		public String type() { return type; }
		public int value() { return value; }
		
		//If idx is not a list, returns id at idx, otherwise returns -1
		public double id(int idx)
		{
			return isList(idx) ? ids.get(idx).get(0) : -1;
		}
		
		//If idx is a list, returns list at idx otherwise returns null
		public ArrayList<Long> idList(int idx)
		{
			return isList(idx) ? ids.get(idx) : null;
		}
		
		private boolean isList(int idx) { return ids.get(idx).size() > 1; }
		
		public String toString()
		{
			String ret = "";
			ret += type;
			ret += "\n";
			for(ArrayList<Long> idList : ids)
			{
				for(double d : idList)
				{
					ret += d;
					ret += " ";
				}
				ret += "\n";
			}
			ret += "Value: ";
			ret += value;
			ret += "\n";
			ret += "String Values: ";
			for(String s : sValues) { ret += s; ret += ", "; }
			ret += "\n";
			
			return ret;
		}
}
