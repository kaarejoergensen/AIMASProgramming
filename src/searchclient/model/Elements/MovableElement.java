package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;

public class MovableElement extends ColeredElement {
    private String ID;
    private String currentTargetId;
    private LinkedList<Node> currentPath;
    private LinkedList<Node> stopBlockingPath;

    public MovableElement(String nodeID, char letter, String color, String ID, String currentTargetId, LinkedList<Node> currentPath, LinkedList<Node> stopBlockingPath) {
        super(nodeID, letter, color);
        this.ID = ID;
        this.currentTargetId = currentTargetId;
        this.currentPath = currentPath;
        this.stopBlockingPath = stopBlockingPath;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCurrentTargetId() {
        return currentTargetId;
    }

    public void setCurrentTargetId(String currentTargetId) {
        this.currentTargetId = currentTargetId;
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

    public boolean hasCurrentTarget() {
        return this.currentTargetId != null && !this.currentTargetId.equals("");
    }
}
