package zork;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private GameState gameState;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        gameState = new GameState();
        readEntitiesFile(entitiesFile);
        readActionsFile(actionsFile);
    }

    private void readEntitiesFile(File entitiesFile){
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            for (int i=0; i < locations.size(); i++){
                Graph location = locations.get(i);
                Node locationDetails = location.getNodes(false).get(0);
                String locationName = locationDetails.getId().getId();
                String locationId = location.getId().getId();
                Location l = new Location(locationId, locationName, locationDetails.getAttribute("description"));
                readSubgraphs(location, l);
                if (i==0){
                    gameState.setStartLocation(l);
                }
                gameState.getGameLocations().put(l.getName(), l);
            }

            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            for (Edge path : paths) {
                Node fromLocation = path.getSource().getNode();
                String fromName = fromLocation.getId().getId();
                Node toLocation = path.getTarget().getNode();
                String toName = toLocation.getId().getId();
                gameState.getGameLocations().get(fromName).addToLocation(toName);
            }

        } catch (FileNotFoundException fnfe) {
            System.out.println("FileNotFoundException was thrown when attempting to read basic entities file");
        } catch (ParseException pe) {
            System.out.println("ParseException was thrown when attempting to read basic entities file");
        }
    }

    private void readSubgraphs(Graph location, Location l){
        for (int i=0; i < location.getSubgraphs().size(); i++){
            Graph subgraph = location.getSubgraphs().get(i);
            String subgraphName = subgraph.getId().getId();
            if (subgraphName.equalsIgnoreCase("artefacts")){
                for (int j=0; j < subgraph.getNodes(false).size(); j++){
                    Node n = subgraph.getNodes(false).get(j);
                    Artefact a = new Artefact(n.getId().getId(), n.getAttribute("description"));
                    l.addArtefact(a);
                }
            }
            if (subgraphName.equalsIgnoreCase("furniture")){
                for (int j=0; j < subgraph.getNodes(false).size(); j++){
                    Node n = subgraph.getNodes(false).get(j);
                    Furniture f = new Furniture(n.getId().getId(), n.getAttribute("description"));
                    l.addFurniture(f);
                }
            }
            if (subgraphName.equalsIgnoreCase("characters")){
                for (int j=0; j < subgraph.getNodes(false).size(); j++){
                    Node n = subgraph.getNodes(false).get(j);
                    Character c = new Character(n.getId().getId(), n.getAttribute("description"));
                    l.addCharacter(c);
                }
            }
        }
    }

    private void readActionsFile(File actionsFile){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getChildNodes();
            for (int j=1; j < actionNodes.getLength(); j += 2){
                Element firstAction = (Element)actionNodes.item(j);
                GameAction g = new GameAction(firstAction);
                ArrayList<String> triggers = g.getTriggers();
                for (String trigger : triggers) {
                    if (gameState.getActions().containsKey(trigger)) {
                        gameState.getActions().get(trigger).add(g);
                    } else {
                        HashSet<GameAction> h = new HashSet<>();
                        h.add(g);
                        gameState.getActions().put(trigger, h);
                    }
                }
            }
        } catch(ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            System.out.println("SAXException was thrown when attempting to read basic actions file");
        } catch(IOException ioe) {
            System.out.println("IOException was thrown when attempting to read basic actions file");
        }
    }

    public String handleCommand(String command) {
        // TODO implement your server logic here
        ArrayList<String> words = splitPlayerCommand(command);
        if (words.size() < 2 || words.get(1).length() == 0){
            return "Please provide command.";
        }
        if (words.get(0).matches(".*[0-9].*")){
            return "No digits in player name please.";
        }
        if (!gameState.getPlayers().containsKey(words.get(0))){
            Player p = new Player(words.get(0), String.valueOf(gameState.getPlayers().size() + 1));
            gameState.setCurrentPlayer(p);
            gameState.getPlayers().put(p.getName(), p);
            gameState.getCurrentPlayer().changeLocation(gameState.getStartLocation());
            gameState.getCurrentPlayer().getCurrentLocation().addPlayer(p);
        }
        else {
            gameState.setCurrentPlayer(gameState.getPlayers().get(words.get(0)));
        }
        CommandHandler c = new CommandHandler(words.get(1), gameState);
        return c.executeCommand();
    }

    public Player getCurrentPlayer(){
        return gameState.getCurrentPlayer();
    }

    public Location getLocation(String locationName){
        return gameState.getGameLocations().get(locationName);
    }

    private ArrayList<String> splitPlayerCommand (String command){
        ArrayList<String> str = new ArrayList<>();
        StringBuilder s = new StringBuilder();
        for (int i=0; i < command.length(); i++){
            if (command.charAt(i) == ':' && str.size() == 0){
                str.add(s.toString());
                s.setLength(0);
            }
            else {
                s.append(command.charAt(i));
            }
        }
        str.add(s.toString());
        return str;
    }

    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }
}
