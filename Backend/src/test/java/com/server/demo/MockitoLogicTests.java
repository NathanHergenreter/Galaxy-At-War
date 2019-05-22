package com.server.demo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.server.demo.Game.*;
import com.server.demo.Model.GMTools;
import com.server.demo.Model.GameManager;
import org.springframework.test.context.TestPropertySource;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MockitoLogicTests {

	@Mock
	private Game mockGame;
	
	@Mock
	private Planet mockPlanet;
	
	@Mock
	private GMTools tools;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void basicTestPass()
	{
		when(mockPlanet.getName()).thenReturn("Mock Planet");
		
		String mockName = mockPlanet.getName();
		
		assertEquals("Mock Planet", mockName);
	}

	/*@Test
	public void basicTestFail()
	{
		when(mockPlanet.getName()).thenReturn("Mock Planet");
		
		String mockName = mockPlanet.getName();
		
		assertEquals("Real Planet", mockName);
	}*/
	
	@Test
	public void fleetDisembarkTest()
	{
		Faction old = new Faction(0, mockGame, null, null, 0);
		Planet p0 = new Planet(0, 0, 0, null, new Climate("Test", 0, 0, 0, 0, 0), 0, 0, 0, 0);
		p0.setOwner(old);
		
		when(tools.findFactionByID(0)).thenReturn(new Faction(0, mockGame, null, null, 0));
		Faction faction = tools.findFactionByID(0);
		
		when(tools.findFleetByID(0, null)).thenReturn(new Fleet(faction, new ArrayList<NavalUnit>(), p0));
		
		Fleet fleet = tools.findFleetByID(0, null);
		faction.addFleet(fleet);
		fleet.getStation().setOwner(new Faction(0, mockGame, null, null, 0));
		
		//Should change ownership after fleet disembarks
		fleet.disembark();
		assertEquals(fleet.getOwner(), p0.getOwner());
	}
	
	@Test
	public void fleetMoveTest()
	{
		Planet p0 = new Planet(0, 0, 0, "p0", new Climate("Test", 0, 0, 0, 0, 0), 0, 0, 0, 0);

		ArrayList<Planet> path = new ArrayList<Planet>();
		Planet p1 = new Planet(0, 0, 0, "p1", new Climate("Test", 0, 0, 0, 0, 0), 0, 0, 0, 0);
		path.add(p1);
		Planet p2 = new Planet(0, 0, 0, "p2", new Climate("Test", 0, 0, 0, 0, 0), 0, 0, 0, 0);
		path.add(p2);
		
		Hyperlane l01 = new Hyperlane(p0, p1, 1, 1);
		p0.addHyperlane(l01);
		p1.addHyperlane(l01);
		Hyperlane l12 = new Hyperlane(p1, p2, 1, 1);
		p1.addHyperlane(l12);
		p2.addHyperlane(l12);
		
		when(tools.findFactionByID(0)).thenReturn(new Faction(0, mockGame, null, null, 0));
		when(tools.findFleetByID(0, tools.findFactionByID(0))).thenReturn(new Fleet(new Faction(0, mockGame, null, null, 0), new ArrayList<NavalUnit>(), p0));
		when(tools.findPlanetByID(0)).thenReturn(p2);
		when(tools.fleetMovePath(tools.findFleetByID(0, tools.findFactionByID(0)), tools.findPlanetByID(0))).thenReturn(path);
		
		//Can't mock void methods? Copy of code from GameManager fleetMoveTo method
		Faction faction = tools.findFactionByID(0);
		Fleet fleet = tools.findFleetByID(0, faction);
		ArrayList<Planet> pathTest = tools.fleetMovePath(tools.findFleetByID(0, faction), tools.findPlanetByID(0));
		fleet.moveTo(pathTest.get(0));
		fleet.queueMove(pathTest.get(1));
		
		//Day 0
		assertEquals(p0, fleet.getStation());
		//Day 1 - Should be mid transit between p0 and p1
		fleet.update();
		assertEquals(l01, fleet.getLane());
		//Day 2 - Should be orbiting p1
		fleet.update();
		assertEquals(p1, fleet.getStation());
		//Day 3 - Should be mid transit between p1 and p2
		fleet.update();
		assertEquals(l12, fleet.getLane());
		//Day 4 - Should be orbiting p2
		fleet.update();
		assertEquals(p2, fleet.getStation());
	}
	
	@Test
	public void shipConstructTest()
	{
		Faction faction = new Faction(0, mockGame, "Test Player", null, 100);
		faction.addLandTemplate(new LandUnitTemplate(faction, "test", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		Planet p0 = new Planet(0, 0, 0, "p0", new Climate("Test", 0, 0, 0, 0, 0), 0, 0, 0, 0);
		p0.setOwner(faction);
		
		when(tools.findFactionByID(0)).thenReturn(faction);
		when(tools.findShipTypeByID(0, tools.findFactionByID(0))).thenReturn(new NavalUnitTemplate(faction, null, "test", 2, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, new ArrayList<String>()));
		when(tools.findPlanetByID(0)).thenReturn(p0);
		
		//Code from buildShip in GameManager
		tools.findPlanetByID(0).buildNavalUnit(tools.findShipTypeByID(0, tools.findFactionByID(0)));
		
		//2 Days left on ship building
		assertEquals(2, p0.getShipDays());
		
		//ShipTypeByID should be in ship building queue
		assertEquals(tools.findShipTypeByID(0, tools.findFactionByID(0)), p0.getShipQueue().get(0));
		
		//Owning faction should have spent money to build ship (cost 100, so from 100 to 0)
		assertEquals(0, faction.getMoney());
		
		//Day 1 - Rem ship build time should decrement to 1
		p0.update();
		assertEquals(1, p0.getShipDays());
		
		//Day 2 - Rem ship build time should be 0, queue should be empty, and new fleet should be made
		p0.update();
		assertEquals(0, p0.getShipDays());
		assertEquals(0, p0.getShipQueue().size());
		assertEquals(1, p0.getFleets().size());
	}

	@Test
	public void FactionTest(){
		when(mockPlanet.getOwner()).thenReturn(new Faction(5872378, mockGame, "testPlayer", "red", 500));
		assertEquals(mockPlanet.getOwner().getPlayer(), "testPlayer");
	}

	@Test
	public void InitUnitTest(){
		Faction f = new Faction(5872378, mockGame, "testPlayer", "red", 500);
		ArrayList<LandUnit> units = new ArrayList<>();
		when(mockPlanet.getOwner()).thenReturn(f);
		mockPlanet.addArmy(new Army(f, units, mockPlanet));
		assertEquals(mockPlanet.getArmies().size(), 1);
	}

	@Test
	public void InitValueTest(){
		when(mockPlanet.getX()).thenReturn(22.0);
		when(mockPlanet.getY()).thenReturn(52.0);

		assertEquals(mockPlanet.getX(), 22.0);
		assertEquals(mockPlanet.getY(), 52.0);
	}

}
