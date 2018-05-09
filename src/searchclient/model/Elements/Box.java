package searchclient.model.Elements;

public class Box extends ColeredElement {

    private String designatedGoal;

    public Box(String nodeID, char letter, String color) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

   public Box(String nodeID, char letter, String color, String designatedGoal) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setDesignatedGoal(designatedGoal);
   }

    @Override
    public Box clone() {
        return new Box(getNodeID(), getLetter(), getColor(), getDesignatedGoal());
    }

    public String getDesignatedGoal() {
        return designatedGoal;
    }

    public void setDesignatedGoal(String designatedGoal) {
        this.designatedGoal = designatedGoal;
    }
}
