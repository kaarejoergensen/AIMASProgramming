package searchclient.model.Elements;

public class Agent extends ColeredElement {
    public Agent(String nodeID, char letter, String color) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

    @Override
    public Agent clone() {
        return new Agent(getNodeID(), getLetter(), getColor());
    }
}
