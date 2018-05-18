package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;
import java.util.Queue;

public class Agent extends MovableElement {
    private boolean currentlyMovingBlockingBox;
    public Agent(String nodeID, char letter, String color, String ID) {
        this(nodeID, letter, color, ID, "", new LinkedList<>(), new LinkedList<>(), false);
    }

    public Agent(String nodeID, char letter, String color, String ID, String currentTargetId, LinkedList<Node> currentPath,
                 LinkedList<Node> stopBlockingPath, boolean currentlyMovingBlockingBox) {
        super(nodeID, letter, color, ID, currentTargetId, currentPath, stopBlockingPath);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.currentlyMovingBlockingBox = currentlyMovingBlockingBox;
    }

    public boolean isCurrentlyMovingBlockingBox() {
        return currentlyMovingBlockingBox;
    }

    public void setCurrentlyMovingBlockingBox(boolean currentlyMovingBlockingBox) {
        this.currentlyMovingBlockingBox = currentlyMovingBlockingBox;
    }

    @Override
    public Agent clone() {
        return new Agent(getNodeID(), getLetter(), getColor(), getID(), getCurrentTargetId(),
                getCurrentPath(), getStopBlockingPath(), isCurrentlyMovingBlockingBox());
    }
}
