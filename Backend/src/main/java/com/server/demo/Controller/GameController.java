package com.server.demo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;
import com.server.demo.Game.Game;
import com.server.demo.Model.ActiveGameSessionUser;
import com.server.demo.Model.GameManager;
import com.server.demo.Model.GameSessionManager;
import com.server.demo.Model.UserManager;
import com.server.demo.Repositories.ActiveGameSocketUserRepository;
import com.server.demo.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
//@RequestMapping(value="/game")
@CrossOrigin(origins="*")
//@CrossOrigin(origins="http://localhost:4200")
//@CrossOrigin(origins="http://cs309-vc-1.misc.iastate.edu:4200")
public class GameController {

    @Autowired
    UserManager userManager;

    @Autowired
    GameManager gameManager = new GameManager();

    @Autowired
    GameSessionManager gameSessionManager = new GameSessionManager();

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Autowired
    private ActiveGameSocketUserRepository gameSessionRepo;

    @MessageMapping("/signal")
    @SendTo("/topic/response")
    public void gameIO(@Payload String message, @Header("simpSessionId") String sessionId) throws Exception{

        GameSignal signal = null;

        ObjectMapper mapper = new ObjectMapper();
        try{
            signal = mapper.readValue(message, GameSignal.class);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        if (signal==null){
            return;
        }

        //List<ActiveGameSessionUser> users =

        if (signal != null){
            System.out.println(signal.getSignal());
        }

        List<ActiveGameSessionUser> recipients = gameSessionRepo.findByGameId(signal.getGameId());
        for (ActiveGameSessionUser user:
             recipients) {
             messageTemplate.convertAndSend("/topic/response/"+sessionId, "Broadcast to "+signal.getGameId());
        }
    }

    private void sendMessageToUser(String destinationId, String msg){
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(destinationId);
        headerAccessor.setLeaveMutable(true);
        messageTemplate.convertAndSendToUser(destinationId, "/topic/response", msg, headerAccessor.getMessageHeaders());
    }

        @PostMapping(value="/game/join")
    @SendTo("/topic/response")
    @ResponseBody
    public String joinGame(@RequestParam(name="username")String username,
                                      @RequestParam(name="gameid")long gameId,
                                      @RequestParam(name="socketid")String sockId){

        User user = userManager.getUser(username);
        if (user == null){
            System.out.println("User not found");
            //return new ResponseEntity<>(username + "not found", HttpStatus.BAD_REQUEST);
            return username + " not found";
        }

        Session session = gameSessionManager.getSession(gameId);
        if (session == null){
            //return new ResponseEntity<>("Game session not found", HttpStatus.BAD_REQUEST);
            return "Game session not found";
        }

        if (session.getSockId()==null){
            session.setSockId(sockId);
        }

        session.addGuest(user);

        //Register user for game comm session
        ActiveGameSessionUser sessionUser = gameSessionRepo.findByUsername(username).get(0);
        sessionUser.setGameId((int)session.getID());
        gameSessionRepo.save(sessionUser);

        //For testing
        if (session.allPlayersJoined() && session.getHost()==null){
            session.setHost(user);
        }

        messageTemplate.convertAndSend("/topic/response/"+session.getSockId(), username + " has joined ("
            + session.getGuests().size() + " of " + session.getPlayerCount()+")");

        //Start game when user queue full
        if (session.allPlayersJoined()){
            gameManager.startGame(session);
            messageTemplate.convertAndSend("/topic/response/"+session.getSockId(), "BEGIN GAME");
            return "BEGIN GAME";
        }

        //return new ResponseEntity<>("", HttpStatus.OK);
        return username + " has joined (" + session.getGuests().size() + " of " + session.getPlayerCount()+")";
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception){
        messageTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

    //Start game (trigger when all game player wait queue full
    @PostMapping(value="/game/startgame")
    public ResponseEntity<?> startGame(@RequestParam(name="gameid") int gameId){



        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping(value="/game/initgame")
    @ResponseBody
    public ResponseEntity<?> initializeGame(@RequestParam(name="hostname") String hostName,
                                            @RequestParam(name="gamename") String gameName,
                                            @RequestParam(name="numplayers") int numPlayers,
                                            @RequestParam(name="numplanets") int numPlanets,
                                            @RequestParam(name="startmoney") int startMoney,
                                            @RequestParam(name="startpoints") int startPoints){

        endGame();

        User host = userManager.getUser(hostName);
        if (host == null) {
            return new ResponseEntity<>("Host not found", HttpStatus.BAD_REQUEST);
        }

        try{
            Session session = new Session(host, gameName, numPlayers, numPlanets, startMoney, startPoints);
            gameSessionManager.addSession(session);
            long gameId = session.getID();
            return new ResponseEntity (gameId, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to start game", HttpStatus.INTERNAL_SERVER_ERROR);
        }        
    }

    @GetMapping("/game/games")
    @ResponseBody
    public String getActiveGames(){

        class GameMetaMap{

            public GameMetaMap(){
                config = new MetaConfig();
            }

            class MetaConfig{
                public String gameName;
                public int playerCount;
                public int planetCount;
                public int startingMoney;
                public int startPoint;
                public long gameId;
            }
            public int playerNeeded;
            public MetaConfig config;

            public String getName(){
                return config.gameName;
            }

            public int getPlayerCount(){
                return config.playerCount;
            }

            public int getPlanetCount(){
                return config.planetCount;
            }

            public int getStartingMoney(){
                return config.startingMoney;
            }

            public int getStartPoint(){
                return config.startPoint;
            }

            public int getPlayerNeeded(){
                return playerNeeded;
            }

            public long getGameId(){
                return config.gameId;
            }
        }

        String result = null;

        ObjectMapper mapper = new ObjectMapper();
        List <Session> sessions = gameSessionManager.getAllSessions();
        ArrayList<GameMetaMap> data = new ArrayList<>();

        for (Session s: sessions) {
            GameMetaMap m = new GameMetaMap();
            m.config.gameId = s.getID();
            m.playerNeeded = s.getPlayerCount();
            m.config.gameName = s.getName();
            m.config.playerCount = s.getReadyCount();
            m.config.planetCount = s.getPlanetCount();
            m.config.startingMoney = s.getStartingMoney();
            m.config.startPoint = s.getStartingPoints();

            data.add(m);
        }

        try{
            result = mapper.writeValueAsString(data);
        } catch(JsonProcessingException e){
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping(value="/game/config")
    @ResponseBody
    public ResponseEntity<?> getGameData(long id){
        if (gameManager == null){
            return new ResponseEntity<>("No current game", HttpStatus.BAD_REQUEST);
        }

        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        HttpStatus status = HttpStatus.OK;
        Game game = gameManager.getGameById(id);

        try{
            result = mapper.writeValueAsString(game);
        } catch(JsonProcessingException e){
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @PostMapping(value="/game/endgame")
    @ResponseBody
    public ResponseEntity<?> endGame(){
        return new ResponseEntity("", HttpStatus.OK);
    }
}


class GameSignal{
    private String signal;//command string
    private Long arg0, arg1, arg2;//argument id's or numerical values
    private String info;//string data (may be serialized into comma separated list)
    private Integer gameId;

    public String getSignal(){
        return signal;
    }

    public void setSignal(String signal){
        this.signal = signal;
    }

    public Long getArg0(){
        return arg0;
    }

    public void setArg0(Long arg0){
        this.arg0 = arg0;
    }

    public Long getArg1() {
        return arg1;
    }

    public void setArg1(Long arg1) {
        this.arg1 = arg1;
    }

    public Long getArg2() {
        return arg2;
    }

    public void setArg2(Long arg2) {
        this.arg2 = arg2;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
