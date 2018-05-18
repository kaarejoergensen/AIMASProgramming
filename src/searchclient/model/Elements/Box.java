package searchclient.model.Elements;

import searchclient.model.Graph;
import searchclient.model.Node;

import java.util.List;
import java.util.NoSuchElementException;

public class Box extends ColeredElement {

    private String designatedGoal;
    private String boxID;

    private List<Node> pathToGoal;

    public Box(String nodeID, char letter, String color) {
        this(nodeID,letter,color,null,nodeID,null);

    }

   public Box(String nodeID, char letter, String color, String designatedGoal, String boxID, List<Node> path) {
        super(nodeID, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
        this.setDesignatedGoal(designatedGoal);
        this.boxID = boxID;

        pathToGoal = path;
   }

    public boolean checkIfPathIsClear(Graph g){
        for(Node n : pathToGoal){
            if(g.getBox(n) != null && g.getBox(n).getBoxID() != this.getBoxID() && g.getBox(n).hasSameColor(this)) return false;
        }
        return true;
    }

    private void checkAndUpdatePath(Graph g){
        if(!checkIfPathIsClear(g)){
            updatePathToGoal(g);
        }
    }

    public void updatePathToGoal(Graph g){
        Node thisBox = g.getBoxNodes().stream().filter(b -> g.getBox(b).getBoxID() == this.getBoxID()).findFirst().get();
        try{
            this.setPathToGoal(g.shortestPath(thisBox, g.getDesignatedGoal(thisBox) ,true,null).get());
        }catch (NullPointerException e){}
        catch (NoSuchElementException e){}
        ;
    }

    public void setPathToGoal(List<Node> pathToGoal) {
        this.pathToGoal = pathToGoal;
    }

    public List<Node> getPathToGoal() {
        return pathToGoal;
    }

    @Override
    public Box clone() {
        return new Box(getNodeID(), getLetter(), getColor(), getDesignatedGoal(), getBoxID(),getPathToGoal());
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
