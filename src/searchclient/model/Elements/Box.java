package searchclient.model.Elements;

import searchclient.model.Node;

public class Box extends ColoredElement {

    private String designatedGoal;

    public Box(String ID, Node node, char letter, String color) {
        this(ID, node, letter, color, null);
    }

    public Box(String ID, Node node, char letter, String color, String designatedGoal) {
        super(ID, node, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.designatedGoal = designatedGoal;
    }

    @Override
    public Box clone() {
        return new Box(getID(), getNode(), getLetter(), getColor(), getDesignatedGoal());
    }

    public String getDesignatedGoal() {
        return designatedGoal;
    }

    public void setDesignatedGoal(String designatedGoal) {
        this.designatedGoal = designatedGoal;
    }
}
