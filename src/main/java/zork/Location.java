package zork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Location extends GameEntity{

    ArrayList<Artefact> artefacts;
    ArrayList<Furniture> furniture;
    ArrayList<Character> characters;
    ArrayList<Player> players;
    Set<String> toLocations;
    String locationID;

    public Location(String id, String name, String description) {
        super(name, description);
        artefacts = new ArrayList<>();
        furniture = new ArrayList<>();
        characters = new ArrayList<>();
        players = new ArrayList<>();
        toLocations = new HashSet<>();
        locationID = id;
    }

    public void addToLocation(String locationName){
        toLocations.add(locationName);
    }

    public void removeToLocation(String locationName){
        toLocations.remove(locationName);
    }

    public void addArtefact(Artefact a){
        artefacts.add(a);
    }

    public void addFurniture(Furniture f){
        furniture.add(f);
    }

    public void addCharacter(Character c){
        characters.add(c);
    }

    public void addPlayer(Player p) { players.add(p); }

    public Set<String> getToLocations(){
        return toLocations;
    }

    public ArrayList<String> getEntities(){
        ArrayList<String> s = new ArrayList<>();
        s.addAll(getArtefactsList());
        s.addAll(getFurnitureList());
        s.addAll(getCharacterList());
        return s;
    }

    public void addEntity(GameEntity g){
        if (g.getClass() == Artefact.class){
            addArtefact((Artefact)g);
        }
        if (g.getClass() == Furniture.class){
            addFurniture((Furniture)g);
        }
        if (g.getClass() == Character.class){
            addCharacter((Character)g);
        }
        if (g.getClass() == Player.class){
            addPlayer((Player)g);
        }
    }

    public GameEntity removeEntity(String entityName){
        GameEntity g = getEntity(entityName);
        if (g.getClass() == Artefact.class){
            artefacts.remove(g);
        }
        if (g.getClass() == Furniture.class){
            furniture.remove(g);
        }
        if (g.getClass() == Character.class){
            characters.remove(g);
        }
        if (g.getClass() == Player.class){
            players.remove(g);
        }
        return g;
    }

    public GameEntity getEntity(String entityName){
        for (Artefact a : artefacts){
            if (a.getName().equals(entityName)){
                artefacts.remove(a);
                return a;
            }
        }
        for (Character c : characters){
            if (c.getName().equals(entityName)){
                characters.remove(c);
                return c;
            }
        }
        for (Furniture f : furniture){
            if (f.getName().equals(entityName)){
                furniture.remove(f);
                return f;
            }
        }
        for (Player p : players){
            if (p.getName().equals(entityName)){
                players.remove(p);
                return p;
            }
        }
        return null;
    }

    public ArrayList<String> getCharacterList(){
        ArrayList<String> s = new ArrayList<>();
        for (Character c : characters){
            s.add(c.getName());
        }
        return s;
    }

    public ArrayList<String> getFurnitureList(){
        ArrayList<String> s = new ArrayList<>();
        for (Furniture f : furniture){
            s.add(f.getName());
        }
        return s;
    }

    public ArrayList<String> getArtefactsList(){
        ArrayList<String> s = new ArrayList<>();
        for (Artefact a : artefacts){
            s.add(a.getName());
        }
        return s;
    }

    public ArrayList<String> getPlayersList(){
        ArrayList<String> s = new ArrayList<>();
        for (Player p : players){
            s.add(p.getName());
        }
        return s;
    }

    public Artefact getArtefact(String artefactName){
        for (Artefact a : artefacts){
            if (a.name.equals(artefactName)){
                return a;
            }
        }
        return null;
    }

    public String printLocation(Player currentPlayer){
        StringBuilder s = new StringBuilder();
        s.append("You are in " + this.name + ", " + this.description + ". You can see:\n");
        for (Artefact a : this.artefacts){
            s.append(a.name + ", " + a.description + "\n");
        }
        for (Furniture f : this.furniture){
            s.append(f.name + ", " + f.description + "\n");
        }
        for (Character c : this.characters){
            s.append(c.name + ", " + c.description + "\n");
        }
        for (Player p : this.players){
            if (!p.getName().equals(currentPlayer.getName())) {
                s.append(p.name + "\n");
            }
        }
        s.append("You can access from here:\n");
        for (String str : this.toLocations){
            s.append(str + "\n");
        }
        return s.toString();
    }

}
