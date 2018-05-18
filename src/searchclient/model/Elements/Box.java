package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;

public class Box extends MovableElement {
    public Box(String nodeID, char letter, String color, String ID) {
        this(nodeID, letter, color, ID, "", new LinkedList<>(), new LinkedList<>());
    }

    public Box(String nodeID, char letter, String color, String ID, String currentTargetId, LinkedList<Node> currentPath, LinkedList<Node> stopBlockingPath) {
        super(nodeID, letter, color, ID, currentTargetId, currentPath, stopBlockingPath);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

    @Override
    public Box clone() {
        return new Box(getNodeID(), getLetter(), getColor(), getID(), getCurrentTargetId(), getCurrentPath(), getStopBlockingPath());
    }
}
