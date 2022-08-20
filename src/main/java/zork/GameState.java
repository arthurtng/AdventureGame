package zork;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeMap;

public class GameState {
    private Hashtable<String, Location> gameLocations;
    private TreeMap<String, HashSet<GameAction>> actions;
    private Hashtable<String, Player> players;
    private Player currentPlayer;
    private Location startLocation;

    public GameState(){
        gameLocations = new Hashtable<>();
        actions = new TreeMap<>();
        players = new Hashtable<>();
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public void setCurrentPlayer(Player p){
        currentPlayer = p;
    }

    public Location getStartLocation(){
        return startLocation;
    }

    public void setStartLocation(Location l){
        startLocation = l;
    }

    public Location getLocation(String locationName){
        return gameLocations.get(locationName);
    }

    public TreeMap<String, HashSet<GameAction>> getActions(){
        return actions;
    }

    public Hashtable<String, Player> getPlayers(){
        return players;
    }

    public Hashtable<String, Location> getGameLocations(){
        return gameLocations;
    }

    public Location findEntity(String entityName){
        for (Location l : gameLocations.values()){
            if (l.getEntities().contains(entityName)){
                return l;
            }
        }
        return null;
    }

}
