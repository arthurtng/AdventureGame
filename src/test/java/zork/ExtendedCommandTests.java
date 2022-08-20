package zork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ExtendedCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("player one: look").toLowerCase();
    assertTrue(response.contains("cabin"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("axe"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("coin"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  // Add more unit tests or integration tests here.
  @Test
  void getAxeInStartLocation() {
    String response = server.handleCommand("player one: get axe").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("axe"), "Axe not in inventory");
  }

  @Test
  void drinkPotion() {
    String response = server.handleCommand("player one: get potion").toLowerCase();
    server.getCurrentPlayer().decreaseHealth();
    assertTrue(server.getCurrentPlayer().getHealth() == 2);
    response = server.handleCommand("player one: drink potion").toLowerCase();
    assertTrue(response.contains("you drink the potion"));
    assertTrue(server.getCurrentPlayer().getHealth() == 3);
    assertTrue(server.getLocation("storeroom").getArtefactsList().contains("potion"));
    assertTrue(!server.getCurrentPlayer().getArtefactsList().contains("potion"));
  }

  @Test
  void emptyInventory() {
    String response = server.handleCommand("player one: inventory").toLowerCase();
    assertTrue(response.contains("<empty>"), "Inventory not empty");
  }

  @Test
  void emptyInv() {
    String response = server.handleCommand("player one: inv").toLowerCase();
    assertTrue(response.contains("<empty>"), "Inventory not empty");
  }

  @Test
  void dropPotionInStartLocation() {
    String response = server.handleCommand("player one: get potion").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not in inventory");
    response = server.handleCommand("player one: drop potion").toLowerCase();
    assertTrue(!server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not dropped");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("potion"), "Potion not dropped");
  }

  @Test
  void gotoForest() {
    String response = server.handleCommand("player one: goto forest").toLowerCase();
    assertTrue(response.contains("forest"), "No description");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("forest"), "Did not go to forest");
  }

  @Test
  void chopTreeInForest() {
    String response = server.handleCommand("player one: get axe").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: cut tree").toLowerCase();
    assertTrue(response.contains("cut down the tree"), "No description");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("log"), "Log did not appear");
    assertTrue(!server.getLocation("storeroom").getArtefactsList().contains("log"), "log not removed");
  }

  @Test
  void getFullHealth() {
    String response = server.handleCommand("player one: health").toLowerCase();
    assertTrue(response.contains("3"), "No health description");
    assertTrue(server.getCurrentPlayer().getHealth()==3, "Health is not 3");
  }

  @Test
  void getTwoItems() {
    String response = server.handleCommand("player one: get potion").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not in inventory");
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("key"), "Key not in inventory");
  }

  @Test
  void unlockTrapDoor() {
    String response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock with key").toLowerCase();
    assertTrue(response.contains("you unlock the door"), "Cellar not unlocked");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getToLocations().contains("cellar"), "Cellar not unlocked");
    assertTrue(!server.getCurrentPlayer().getArtefactsList().contains("key"), "key still in inv");
    assertTrue(server.getLocation("storeroom").getArtefactsList().contains("key"), "key not moved to storeroom");
  }

  @Test
  void gotoCellar() {
    String response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    assertTrue(response.contains("a dusty cellar"), "did not go to cellar");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("cellar"), "did not go to cellar");

  }

  @Test
  void hitElf() {
    String response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: hit elf").toLowerCase();
    assertTrue(response.contains("you attack the elf"), "did not fight the elf");
    assertTrue(server.getCurrentPlayer().getHealth()==2, "did not lose 1 health");

  }

  @Test
  void getShovel() {
    String response = server.handleCommand("player one: get coin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: pay elf").toLowerCase();
    response = server.handleCommand("player one: get shovel").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("shovel"), "did not get shovel");
    assertTrue(!server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("shovel"), "did not remove shovel");
  }

  @Test
  void blowHorn() {
    String response = server.handleCommand("player one: get coin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: pay elf").toLowerCase();
    response = server.handleCommand("player one: get shovel").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: goto riverbank").toLowerCase();
    response = server.handleCommand("player one: get horn").toLowerCase();
    response = server.handleCommand("player one: blow horn").toLowerCase();
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getCharacterList().contains("lumberjack"), "did not produce lumberjack");
    assertTrue(!server.getLocation("storeroom").getCharacterList().contains("lumberjack"), "did not remove lumberjack from storeroom");
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: blow horn").toLowerCase();
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getCharacterList().contains("lumberjack"), "did not produce lumberjack");
  }

  @Test
  void bridgeRiver() {
    String response = server.handleCommand("player one: get coin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: pay elf").toLowerCase();
    response = server.handleCommand("player one: get shovel").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: goto riverbank").toLowerCase();
    response = server.handleCommand("player one: bridge river").toLowerCase();
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getToLocations().contains("clearing"), "did not produce clearing");
    assertTrue(server.getLocation("storeroom").getArtefactsList().contains("log"), "did not move log to storeroom");
  }

  @Test
  void digGround() {
    String response = server.handleCommand("player one: get coin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: pay elf").toLowerCase();
    response = server.handleCommand("player one: get shovel").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: goto riverbank").toLowerCase();
    response = server.handleCommand("player one: bridge river").toLowerCase();
    response = server.handleCommand("player one: goto clearing").toLowerCase();
    response = server.handleCommand("player one: dig ground").toLowerCase();
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("gold"), "did not produce gold");
    assertTrue(server.getLocation("storeroom").getFurnitureList().contains("ground"), "did not move ground to storeroom");
  }

  @Test
  void payElf() {
    String response = server.handleCommand("player one: get coin").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: pay elf").toLowerCase();
    assertTrue(response.contains("you pay the elf"), "did not pay the elf");
    assertTrue(!server.getCurrentPlayer().getArtefactsList().contains("coin"), "did not remove coin from inventory");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("shovel"), "did not produce shovel");
    assertTrue(server.getLocation("storeroom").getArtefactsList().contains("coin"), "did not move coin to storeroom");
  }

  @Test
  void deathFromZeroHealth() {
    String response = server.handleCommand("player one: get potion").toLowerCase();
    response = server.handleCommand("player one: goto forest").toLowerCase();
    response = server.handleCommand("player one: get key").toLowerCase();
    response = server.handleCommand("player one: goto cabin").toLowerCase();
    response = server.handleCommand("player one: unlock trapdoor").toLowerCase();
    response = server.handleCommand("player one: goto cellar").toLowerCase();
    response = server.handleCommand("player one: fight elf").toLowerCase();
    response = server.handleCommand("player one: fight elf").toLowerCase();
    response = server.handleCommand("player one: fight elf").toLowerCase();
    assertTrue(response.contains("you died and lost all of your items"), "did not die");
    assertTrue(server.getCurrentPlayer().getArtefactsList().isEmpty(), "did not lose all your items");
    assertTrue(server.getLocation("cellar").getArtefactsList().contains("potion"));
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("cabin"), "did not go back to starting position");

  }

  @Test
  void twoPlayers() {
    String response = server.handleCommand("player one: look").toLowerCase();
    response = server.handleCommand("player two: look").toLowerCase();
    assertTrue(response.contains("player one"), "Player one not shown");
    response = server.handleCommand("player one: look").toLowerCase();
    assertTrue(response.contains("player two"), "Player two not shown");
  }

  @Test
  void threePlayers() {
    String response = server.handleCommand("player one: look").toLowerCase();
    response = server.handleCommand("player two: look").toLowerCase();
    response = server.handleCommand("player three: look").toLowerCase();
    assertTrue(response.contains("player one"), "Player one not shown");
    assertTrue(response.contains("player two"), "Player two not shown");
    response = server.handleCommand("player one: look").toLowerCase();
    assertTrue(response.contains("player two"), "Player two not shown");
    assertTrue(response.contains("player three"), "Player three not shown");
  }

}
