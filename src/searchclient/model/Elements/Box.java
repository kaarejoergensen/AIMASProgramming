package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.LinkedList;

public class Box extends ColeredElement {

    private String designatedGoal;
    private LinkedList<Node> currentPath;
    private String boxID;

    public Box(String nodeID, char letter, String color) {
        this(nodeID, letter, color, null, nodeID, new LinkedList<>());

    }

    public Box(String nodeID, char letter, String color, String designatedGoal, String boxID, LinkedList<Node> currentPath) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setDesignatedGoal(designatedGoal);
        this.boxID = boxID;
        this.currentPath = currentPath;
    }

    @Override
    public Box clone() {
        return new Box(getNodeID(), getLetter(), getColor(), getDesignatedGoal(), getBoxID(), getCurrentPath());
    }

    public String getDesignatedGoal() {
        return designatedGoal;
    }

    public void setDesignatedGoal(String designatedGoal) {
        this.designatedGoal = designatedGoal;
    }

    public String getBoxID() {
        return boxID;
    }

    public LinkedList<Node> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(LinkedList<Node> currentPath) {
        this.currentPath = currentPath;
    }
}
