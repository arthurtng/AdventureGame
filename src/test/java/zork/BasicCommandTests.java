package zork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("playerOne: look").toLowerCase();
    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  // Add more unit tests or integration tests here.
  @Test
  void testNoCommand() {
    String response = server.handleCommand("playerOne:").toLowerCase();
    System.out.println(response);
    assertTrue(response.contains("please provide command"), "Should not respond to no command");
  }

  @Test
  void testSemicolon() {
    String response = server.handleCommand("playerOne:;").toLowerCase();
    assertTrue(response.contains("thanks for your message"), "Should not respond to bad command");
  }

  @Test
  void testColon() {
    String response = server.handleCommand("playerOne::").toLowerCase();
    assertTrue(response.contains("thanks for your message"), "Should not respond to bad command");
  }

  @Test
  void testEmbeddedColon() {
    String response = server.handleCommand("playerOne: :look:").toLowerCase();
    System.out.println(response);
    assertTrue(response.contains("thanks for your message"), "Should not respond to bad command");
  }

  @Test
  void getPotionInStartLocation() {
    String response = server.handleCommand("playerOne: get potion").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not in inventory");
  }

  @Test
  void emptyInventory() {
    String response = server.handleCommand("playerOne: inventory").toLowerCase();
    assertTrue(response.contains("<empty>"), "Inventory not empty");
  }

  @Test
  void emptyInv() {
    String response = server.handleCommand("playerOne: inv").toLowerCase();
    assertTrue(response.contains("<empty>"), "Inventory not empty");
  }

  @Test
  void dropPotionInStartLocation() {
    String response = server.handleCommand("playerOne: get potion").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not in inventory");
    response = server.handleCommand("playerOne: drop potion").toLowerCase();
    assertTrue(!server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not dropped");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getArtefactsList().contains("potion"), "Potion not dropped");
  }

  @Test
  void gotoForest() {
    String response = server.handleCommand("playerOne: goto forest").toLowerCase();
    assertTrue(response.contains("forest"), "No description");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("forest"), "Did not go to forest");
  }

  @Test
  void getFullHealth() {
    String response = server.handleCommand("playerOne: health").toLowerCase();
    assertTrue(response.contains("3"), "No health description");
    assertTrue(server.getCurrentPlayer().getHealth()==3, "Health is not 3");
  }

  @Test
  void getTwoItems() {
    String response = server.handleCommand("playerOne: get potion").toLowerCase();
    response = server.handleCommand("playerOne: goto forest").toLowerCase();
    response = server.handleCommand("playerOne: get key").toLowerCase();
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("potion"), "Potion not in inventory");
    assertTrue(server.getCurrentPlayer().getArtefactsList().contains("key"), "Key not in inventory");
  }

  @Test
  void unlockTrapDoor() {
    String response = server.handleCommand("playerOne: goto forest").toLowerCase();
    response = server.handleCommand("playerOne: get key").toLowerCase();
    response = server.handleCommand("playerOne: goto cabin").toLowerCase();
    response = server.handleCommand("playerOne: unlock with key").toLowerCase();
    assertTrue(response.contains("you unlock the trapdoor"), "Cellar not unlocked");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getToLocations().contains("cellar"), "Cellar not unlocked");

  }

  @Test
  void gotoCellar() {
    String response = server.handleCommand("playerOne: goto forest").toLowerCase();
    response = server.handleCommand("playerOne: get key").toLowerCase();
    response = server.handleCommand("playerOne: goto cabin").toLowerCase();
    response = server.handleCommand("playerOne: unlock trapdoor").toLowerCase();
    response = server.handleCommand("playerOne: goto cellar").toLowerCase();
    assertTrue(response.contains("a dusty cellar"), "did not go to cellar");
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("cellar"), "did not go to cellar");

  }

  @Test
  void fightElf() {
    String response = server.handleCommand("playerOne: goto forest").toLowerCase();
    response = server.handleCommand("playerOne: get key").toLowerCase();
    response = server.handleCommand("playerOne: goto cabin").toLowerCase();
    response = server.handleCommand("playerOne: unlock trapdoor").toLowerCase();
    response = server.handleCommand("playerOne: goto cellar").toLowerCase();
    response = server.handleCommand("playerOne: fight elf").toLowerCase();
    assertTrue(response.contains("you attack the elf"), "did not fight the elf");
    assertTrue(server.getCurrentPlayer().getHealth()==2, "did not lose 1 health");

  }

  @Test
  void deathFromZeroHealth() {
    String response = server.handleCommand("playerOne: get potion").toLowerCase();
    response = server.handleCommand("playerOne: goto forest").toLowerCase();
    response = server.handleCommand("playerOne: get key").toLowerCase();
    response = server.handleCommand("playerOne: goto cabin").toLowerCase();
    response = server.handleCommand("playerOne: unlock trapdoor").toLowerCase();
    response = server.handleCommand("playerOne: goto cellar").toLowerCase();
    response = server.handleCommand("playerOne: fight elf").toLowerCase();
    response = server.handleCommand("playerOne: fight elf").toLowerCase();
    response = server.handleCommand("playerOne: fight elf").toLowerCase();
    assertTrue(response.contains("you died and lost all of your items"), "did not die");
    assertTrue(server.getCurrentPlayer().getArtefactsList().isEmpty(), "did not lose all your items");
    assertTrue(server.getLocation("cellar").getArtefactsList().contains("potion"));
    assertTrue(server.getCurrentPlayer().getCurrentLocation().getName().equals("cabin"), "did not go back to starting position");

  }

}
