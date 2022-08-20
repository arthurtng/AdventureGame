package zork;

import java.util.ArrayList;

public class Player extends Character{
    private ArrayList<Artefact> inventory;
    private int health;
    private Location currentLocation;

    public Player(String name, String description) {

        super(name, description);
        inventory = new ArrayList<>();
        health = 3;
    }

    public void changeLocation(Location newLocation){
        currentLocation = newLocation;
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public void addToInventory(Artefact pickedUp){
        inventory.add(pickedUp);
    }

    public int getHealth(){
        return health;
    }

    public void addHealth(){
        health++;
    }

    public void decreaseHealth(){
        health--;
    }

    public void die(){
        health = 3;
        inventory.clear();
    }

    public ArrayList<Artefact> getInventory(){
        return inventory;
    }

    public Artefact dropArtefact(String artefactName){
        for (Artefact a : inventory){
            if (a.getName().equals(artefactName)){
                inventory.remove(a);
                return a;
            }
        }
        return null;
    }

    public Artefact getArtefact(String artefactName){
        for (Artefact a : inventory){
            if (a.getName().equals(artefactName)){
                return a;
            }
        }
        return null;
    }

    public ArrayList<String> getArtefactsList(){
        ArrayList<String> s = new ArrayList<>();
        for (Artefact a : inventory){
            s.add(a.getName());
        }
        return s;
    }

    public Artefact consumeArtefact(String artefactName){
        for (Artefact i : inventory){
            if (i.getName().equals(artefactName)){
                inventory.remove(i);
                return i;
            }
        }
        return null;
    }

    public String printInventory(){
        StringBuilder s = new StringBuilder();
        for (Artefact a : inventory){
            s.append(a.getName() + "\n");
        }
        if (s.isEmpty()){
            s.append("<Empty>\n");
        }
        return s.toString();
    }

}
