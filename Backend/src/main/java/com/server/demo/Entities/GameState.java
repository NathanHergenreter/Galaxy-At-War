package com.server.demo.Entities;

import javax.persistence.*;

//Might be useful to have a snapshot of an active game
//Most live game data can be stored in websocket sessions

@Entity
@Table(name = "gamestate")
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;



    protected GameState(){}

}
