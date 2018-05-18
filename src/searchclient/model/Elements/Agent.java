package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;
import java.util.Queue;

public class Agent extends MovableElement {
    private String stopBlockingTargetID;

    public Agent(String nodeID, char letter, String color, String ID) {
        this(nodeID, letter, color, ID, "", new LinkedList<>(), "", new LinkedList<>());
    }

    public Agent(String nodeID, char letter, String color, String ID, String currentTargetId, LinkedList<Node> currentPath, String stopBlockingTargetID, LinkedList<Node> stopBlockingPath) {
        super(nodeID, letter, color, ID, currentTargetId, currentPath, stopBlockingPath);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.stopBlockingTargetID = stopBlockingTargetID;
    }

    public String getStopBlockingTargetID() {
        return stopBlockingTargetID;
    }

    public void setStopBlockingTargetID(String stopBlockingTargetID) {
        this.stopBlockingTargetID = stopBlockingTargetID;
    }

    @Override
    public Agent clone() {
        return new Agent(getNodeID(), getLetter(), getColor(), getID(), getCurrentTargetId(), getCurrentPath(), getStopBlockingTargetID(), getStopBlockingPath());
    }
}
