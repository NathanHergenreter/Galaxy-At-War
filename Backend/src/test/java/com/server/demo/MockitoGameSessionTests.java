package com.server.demo;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

import com.server.demo.Model.GameManager;
import com.server.demo.Model.UserManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.server.demo.Entities.PlayerColor;
import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;
import com.server.demo.Model.GameSessionManager;



@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MockitoGameSessionTests {

	@Mock
	private GameManager mockGameManager;

	@Mock
	private UserManager userManager;

	@Test
	public void addUserTest(){
		User host = new User("TestHost", "Password");

		userManager.addUser(host);
		when(userManager.getUser(host.getName())).thenReturn(userManager.getUser(host.getName()));
		assertEquals("TestHost", userManager.getUser("TestHost").getName());
		User tmp = userManager.getUser(host.getName());
		when(userManager.getUser(host.getName())).thenReturn(null);
		userManager.deleteUser(tmp);
		assertEquals(userManager.getUser(host.getName()), null);
	}

	@Mock
	private GameSessionManager mockManager;
	
	User testHost = new User("TestHost", "Password");
	User testUser = new User("TestGuest1", "Password");
	User testUser2 = new User("TestGuest2", "Password");
	String colorHost = "#283AD6";
	String color = "#D61919";
	String color2 = "#2ED33C";
	long gameID = 0;
	
	@Test
	public void joinGameTest() {
		when(mockManager.getSession(gameID)).thenReturn(new Session(testHost, "Test Game", 0, 0, 0, 0));

		Session session = mockManager.getSession(gameID);
		session.addGuest(testUser);
		session.addGuest(testUser2);
		
		assertEquals(session.getColors().size(), 3);
		assertEquals(session.getColor(testUser), color);
		assertEquals(session.getColor(testUser2), color2);
		assertEquals(session.getColor(testHost), colorHost);
	}
	
	@Test
	public void leaveJoinGameTest() {
		when(mockManager.getSession(gameID)).thenReturn(new Session(testHost, "Test Game", 0, 0, 0, 0));

		Session session = mockManager.getSession(gameID);
		session.addGuest(testUser);
		assertEquals(session.getColors().size(), 2);
		assertEquals(session.getColor(testUser), color);
		
		session.removeGuest(testUser);
		assertEquals(session.getColors().size(), 1);

		session.addGuest(testUser2);
		assertEquals(session.getColors().size(), 2);
		assertEquals(session.getColor(testUser2), color);
		
		session.removeGuest(testHost);
		assertEquals(session.getColors().size(), 1);
		
		session.addGuest(testUser);
		assertEquals(session.getColors().size(), 2);
		assertEquals(session.getColor(testUser), color2);
	}
}
