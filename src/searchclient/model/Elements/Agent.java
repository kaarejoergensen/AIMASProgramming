package searchclient.model.Elements;

import searchclient.model.Node;

public class Agent extends ColoredElement {
    private String currentBoxID;


    public Agent(String ID, Node node, char letter, String color) {
        this(ID, node, letter, color, null);
    }

    public Agent(String ID, Node node, char letter, String color, String currentBoxID) {
        super(ID, node, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.currentBoxID = currentBoxID;
    }

    @Override
    public Agent clone() {
        return new Agent(getID(), getNode(), getLetter(), getColor(), getCurrentBoxID());
    }

    public String getCurrentBoxID() {
        return currentBoxID;
    }

    public void setCurrentBoxID(String currentBoxID) {
        this.currentBoxID = currentBoxID;
    }
}
