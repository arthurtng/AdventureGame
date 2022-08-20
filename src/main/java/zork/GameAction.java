package zork;

import org.w3c.dom.Element;

import java.util.ArrayList;

public class GameAction
{
    ArrayList<String> triggers;
    ArrayList<String> subjects;
    ArrayList<String> consumed;
    ArrayList<String> produced;
    String narration;
    public GameAction(Element n)
    {
        triggers = new ArrayList<>();
        subjects = new ArrayList<>();
        consumed = new ArrayList<>();
        produced = new ArrayList<>();

        Element triggers = (Element)n.getElementsByTagName("triggers").item(0);
        Element subjects = (Element)n.getElementsByTagName("subjects").item(0);
        Element consumed = (Element)n.getElementsByTagName("consumed").item(0);
        Element produced = (Element)n.getElementsByTagName("produced").item(0);
        narration = n.getElementsByTagName("narration").item(0).getTextContent();
        for (int j=0; j < triggers.getElementsByTagName("keyword").getLength(); j++){
            String triggerPhrase = triggers.getElementsByTagName("keyword").item(j).getTextContent();
            this.triggers.add(triggerPhrase);
        }
        for (int k=0; k < subjects.getElementsByTagName("entity").getLength(); k++){
            String subjectPhrase = subjects.getElementsByTagName("entity").item(k).getTextContent();
            this.subjects.add(subjectPhrase);
        }
        for (int l=0; l < consumed.getElementsByTagName("entity").getLength(); l++){
            String consumedPhrase = consumed.getElementsByTagName("entity").item(l).getTextContent();
            this.consumed.add(consumedPhrase);
        }
        for (int m=0; m < produced.getElementsByTagName("entity").getLength(); m++){
            String producedPhrase = produced.getElementsByTagName("entity").item(m).getTextContent();
            this.produced.add(producedPhrase);
        }
    }

    public ArrayList<String> getProduced(){
        return produced;
    }

    public ArrayList<String> getConsumed(){
        return consumed;
    }

    public ArrayList<String> getTriggers(){
        return this.triggers;
    }

    public String getNarration() {
        return narration;
    }

    public ArrayList<String> getSubjects(){
        return this.subjects;
    }

    public boolean commandIncludesSubject(String command){
        String[] splitCommand = command.split(" ");
        for (String s : splitCommand){
            if (this.getSubjects().contains(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public boolean areSubjectsPresent(Location l, Player p){
        int flag = 0;
        for (String subject : subjects){
            int innerFlag = 0;
            for (String entity : l.getEntities()){
                if (subject.equals(entity)) {
                    innerFlag = 1;
                    break;
                }
            }
            for (String artefact : p.getArtefactsList()){
                if (subject.equals(artefact)) {
                    innerFlag = 1;
                    break;
                }
            }
            flag = innerFlag;
        }
        return flag == 1;
    }

}
