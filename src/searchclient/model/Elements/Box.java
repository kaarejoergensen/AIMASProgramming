package searchclient.model.Elements;

public class Box extends ColeredElement {
    public Box(String nodeID, char letter, String color) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

    @Override
    public Box clone() {
        return new Box(getNodeID(), getLetter(), getColor());
    }
}
