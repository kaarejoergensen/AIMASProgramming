package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;
import java.util.Queue;

public class Agent extends ColeredElement {
    public String currentBoxID;
    private LinkedList<Node> currentPath;
    private LinkedList<Node> stopBlockingPath;


    public Agent(String nodeID, char letter, String color) {
        this(nodeID, letter, color, "", new LinkedList<>(), new LinkedList<>());
    }

    public Agent(String nodeID, char letter, String color, String currentBoxID, LinkedList<Node> currentPath, LinkedList<Node> stopBlockingPath) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setCurrentBoxID(currentBoxID);
        this.currentPath = currentPath;
        this.stopBlockingPath = stopBlockingPath;
    }

    @Override
    public Agent clone() {
        return new Agent(getNodeID(), getLetter(), getColor(), getCurrentBoxID(), getCurrentPath(), getStopBlockingPath());
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

    public LinkedList<Node> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(LinkedList<Node> currentPath) {
        this.currentPath = currentPath;
    }

    public LinkedList<Node> getStopBlockingPath() {
        return stopBlockingPath;
    }

    public void setStopBlockingPath(LinkedList<Node> stopBlockingPath) {
        this.stopBlockingPath = stopBlockingPath;
    }
}
