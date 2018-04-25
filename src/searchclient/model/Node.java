package searchclient.model;

import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.ColeredElement;
import searchclient.model.Elements.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private Integer x;
    private Integer y;
    private ColeredElement agent;
    private ColeredElement box;
    private ColeredElement goal;
    private List<Edge> edges;

    public Node(int x, int y, ColeredElement element) {
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
        if (element instanceof Agent) {
            this.agent = element;
        } else if (element instanceof Box) {
            this.box = element;
        } else {
            this.goal = element;
        }
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    public String getId() {
        return String.valueOf(x) + ',' + String.valueOf(y);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public ColeredElement getAgent() {
        return agent;
    }

    public void setAgent(ColeredElement agent) {
        this.agent = agent;
    }

    public ColeredElement getBox() {
        return box;
    }

    public void setBox(ColeredElement box) {
        this.box = box;
    }

    public ColeredElement getGoal() {
        return goal;
    }

    public void setGoal(ColeredElement goal) {
        this.goal = goal;
    }

    public boolean canBeMovedTo() {
        return this.agent == null && this.box == null;
    }

    @Override
    public Node clone() {
        Node node = new Node(this.x, this.y);

        node.agent = this.agent;
        node.box = this.box;
        node.goal = this.goal;

        node.edges = this.edges;

        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(x, node.x) &&
                Objects.equals(y, node.y) &&
                Objects.equals(agent, node.agent) &&
                Objects.equals(box, node.box) &&
                Objects.equals(goal, node.goal) &&
                Objects.equals(edges, node.edges);
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, agent, box, goal, edges);
    }
}
