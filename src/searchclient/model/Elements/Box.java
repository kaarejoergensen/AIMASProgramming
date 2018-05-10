package searchclient.model.Elements;

public class Box extends ColeredElement {

    private String designatedGoal;
    private String boxID;

    public Box(String nodeID, char letter, String color) {
        this(nodeID,letter,color,null);
    }

   public Box(String nodeID, char letter, String color, String designatedGoal) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setDesignatedGoal(designatedGoal);
        this.boxID = nodeID;
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

    public String getBoxID() {
        return boxID;
    }
}
