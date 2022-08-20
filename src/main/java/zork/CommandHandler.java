package zork;

import java.util.ArrayList;
import java.util.Set;

public class CommandHandler {
    String command;
    GameState gameState;

    public CommandHandler(String command, GameState g){
        this.command = command;
        gameState = g;
    }

    public String executeCommand(){
        String[] commandWords = command.trim().split(" ");
        if (commandWords[0].equalsIgnoreCase("inv") || commandWords[0].equalsIgnoreCase(("inventory"))){
            return inv();
        }
        if (commandWords[0].equalsIgnoreCase("look")){
            return look();
        }
        if (commandWords[0].equalsIgnoreCase("get")){
            return get();
        }
        if (commandWords[0].equalsIgnoreCase("drop")){
            return drop();
        }
        if (commandWords[0].equalsIgnoreCase("goto")){
            return goTo();
        }
        if (commandWords[0].equalsIgnoreCase("health")){
            return health();
        }
        // Check actions
        String action = checkActions(command);
        if (action != null){
            return action;
        }
        return "Thanks for your message: " + command;
    }

    private String inv(){
        return gameState.getPlayers().get(gameState.getCurrentPlayer().getName()).printInventory();
    }

    private String look(){
        return gameState.getCurrentPlayer().getCurrentLocation().printLocation(gameState.getCurrentPlayer());
    }

    private String get(){
        ArrayList<String> artefactList = gameState.getCurrentPlayer().getCurrentLocation().getArtefactsList();
        // One artefact at a time
        int count = 0;
        Artefact pickedUp = null;
        for (String artefactName : artefactList){
            if (command.toLowerCase().contains(artefactName)){
                pickedUp = gameState.getCurrentPlayer().getCurrentLocation().getArtefact(artefactName);
                count++;
            }
        }
        if (count == 1 && pickedUp != null){
            gameState.getCurrentPlayer().getCurrentLocation().removeEntity(pickedUp.getName());
            gameState.getCurrentPlayer().addToInventory(pickedUp);
            return "Picked up " + pickedUp.getName() + "\n";
        }
        return "Item does not exist\n";
    }

    private String goTo(){
        for (String name : gameState.getCurrentPlayer().getCurrentLocation().getToLocations()){
            if (command.toLowerCase().contains(name)){
                gameState.getCurrentPlayer().getCurrentLocation().removeEntity(gameState.getCurrentPlayer().getName());
                gameState.getCurrentPlayer().changeLocation(gameState.getGameLocations().get(name));
                gameState.getCurrentPlayer().getCurrentLocation().addPlayer(gameState.getCurrentPlayer());
            }
        }
        return gameState.getCurrentPlayer().getCurrentLocation().printLocation(gameState.getCurrentPlayer());
    }

    private String drop(){
        ArrayList<String> inventory = gameState.getCurrentPlayer().getArtefactsList();
        int count = 0;
        Artefact dropped = null;
        for (String artefactName : inventory){
            if (command.toLowerCase().contains(artefactName)){
                dropped = gameState.getCurrentPlayer().getArtefact(artefactName);
                count++;
            }
        }
        if (count == 1 && dropped != null){
            gameState.getCurrentPlayer().dropArtefact(dropped.getName());
            gameState.getCurrentPlayer().getCurrentLocation().addArtefact(dropped);
            return "Dropped " + dropped.getName() + "\n";
        }
        return "Item does not exist\n";
    }

    private String health(){
        return "Health: " + gameState.getCurrentPlayer().getHealth();
    }

    private String checkActions(String command){
        Set<String> actionKeys = gameState.getActions().keySet();
        for (String actionKey : actionKeys){
            if (command.toLowerCase().contains(actionKey)){
                // Check for ambiguous commands
                ArrayList<GameAction> toBeExecuted = new ArrayList<>();
                for (GameAction g : gameState.getActions().get(actionKey)){
                    if (g.commandIncludesSubject(command) && g.areSubjectsPresent(gameState.getCurrentPlayer().getCurrentLocation(), gameState.getCurrentPlayer())){
                        toBeExecuted.add(g);
                    }
                }
                if (toBeExecuted.size() == 1){
                    return executeAction(toBeExecuted.get(0));
                }
                else if (toBeExecuted.size() > 1 && resolveAmbiguity(toBeExecuted) != null){
                    return executeAction(resolveAmbiguity(toBeExecuted));
                }
                else if (toBeExecuted.size() > 1 && resolveAmbiguity(toBeExecuted) == null){
                    return "there is more than one thing you can \'" + actionKey + "\' here - which one do you want ?\n";
                }
            }
        }
        return null;
    }

    private String executeAction(GameAction g){
        StringBuilder s = new StringBuilder();
        for (String p : g.getProduced()){
            // Add locations
            if (gameState.getGameLocations().containsKey(p)){
                gameState.getCurrentPlayer().getCurrentLocation().addToLocation(p);
            }
            // Add entities
            else if (gameState.findEntity(p) != null){
                Location l = gameState.findEntity(p);
                GameEntity entity = gameState.getLocation(l.getName()).removeEntity(p);
                gameState.getCurrentPlayer().getCurrentLocation().addEntity(entity);
            }
            // Add health
            else if (p.contains("health")){
                if (gameState.getCurrentPlayer().getHealth() < 3) {
                    gameState.getCurrentPlayer().addHealth();
                    s.append("You gained 1 health.\n");
                }
                else {
                    s.append("Your health is already full.\n");
                }
            }
        }
        for (String c : g.getConsumed()){
            // Remove location
            if (gameState.getGameLocations().containsKey(c)){
                gameState.getCurrentPlayer().getCurrentLocation().removeToLocation(c);
            }
            // Remove entity
            else if (gameState.findEntity(c) != null){
                Location l = gameState.findEntity(c);
                GameEntity entity = gameState.getLocation(l.getName()).removeEntity(c);
                gameState.getGameLocations().get("storeroom").addEntity(entity);
            }
            // Remove artefact from player inv
            else if (gameState.getCurrentPlayer().getArtefactsList().contains(c)){
                Artefact a = gameState.getCurrentPlayer().consumeArtefact(c);
                gameState.getGameLocations().get("storeroom").addArtefact(a);
            }
            // Decrease health
            else if (c.contains("health")){
                gameState.getCurrentPlayer().decreaseHealth();
                s.append("You lost 1 health.\n");
            }
        }
        s.append(g.getNarration()).append("\n");
        s.append(checkHealth());
        return s.toString();
    }

    private String checkHealth(){
        if (gameState.getCurrentPlayer().getHealth() <= 0){
            for (Artefact a : gameState.getCurrentPlayer().getInventory()){
                gameState.getCurrentPlayer().getCurrentLocation().addArtefact(a);
            }
            gameState.getCurrentPlayer().die();
            gameState.getCurrentPlayer().changeLocation(gameState.getStartLocation());
            return "you died and lost all of your items, you must return to the start of the game";
        }
        return "";
    }

    private GameAction resolveAmbiguity(ArrayList<GameAction> toBeExecuted){
        ArrayList<Integer> counts = new ArrayList<>();
        for (int j = 0; j < toBeExecuted.size(); j++){
            counts.add(0);
            for (int i=0; i < toBeExecuted.get(j).getSubjects().size(); i++){
                if (command.toLowerCase().contains(toBeExecuted.get(j).getSubjects().get(i))){
                    counts.set(j, counts.get(j)+1);
                }
            }
        }
        if (getMaxIndex(counts) != -1){
            return toBeExecuted.get(getMaxIndex(counts));
        }
        return null;
    }

    private int getMaxIndex(ArrayList<Integer> counts){
        int maxCount = 0;
        int max = 0;
        int maxIndex = 0;
        for (int j=0; j < counts.size(); j++){
            if (counts.get(j) > max){
                max = counts.get(j);
                maxIndex = j;
            }
        }
        for (int k : counts){
            if (k == max){
                maxCount++;
            }
        }
        if (maxCount == 1){
            return maxIndex;
        }
        return -1;
    }

}
