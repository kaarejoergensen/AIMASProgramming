package searchclient.model.Elements;

import searchclient.model.Node;
import java.util.Queue;

public class Agent extends ColeredElement {
    public String currentBoxID;


    public Agent(String nodeID, char letter, String color) {
        this(nodeID,letter,color,"");
    }

    public Agent(String nodeID, char letter, String color, String currentBoxID){
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setCurrentBoxID(currentBoxID);
    }

    @Override
    public Agent clone() {
        return new Agent(getNodeID(), getLetter(), getColor(),getCurrentBoxID());
    }

    public String getCurrentBoxID() {
        return currentBoxID;
    }

    public void setCurrentBoxID(String currentBoxID) {
        this.currentBoxID = currentBoxID;
    }

    public boolean hasCurrentBoxID() {
        return this.currentBoxID != null && !this.getCurrentBoxID().equals("");
    }
}
