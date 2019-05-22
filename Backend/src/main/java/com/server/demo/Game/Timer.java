package com.server.demo.Game;

import java.util.concurrent.TimeUnit;

//Keeps track of time
//Every interval call update on Game
public class Timer implements Runnable {
	
	private static final int interval = 5;
	private Game game;
	private volatile boolean exit;

	public Timer(Game game)
	{
		this.game = game;
		this.exit = false;
	}
	
	@Override
	//Called by new thread
	//Starts timer
	public void run() {
		while(!exit)
		{
			//Waits 5 seconds before updating
			try {
				TimeUnit.SECONDS.sleep(interval);
			} 
			catch (InterruptedException e) { }
			
			game.update();
		}
		//System.out.println(Thread.currentThread().getName()+" stopping");
	}
	
	//Called by main thread
	//Stops timer from running
	public void stop()
	{
		this.exit = true;
	}

}
