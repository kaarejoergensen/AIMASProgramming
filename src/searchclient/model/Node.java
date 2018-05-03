package searchclient.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private String ID;
    private Integer x;
    private Integer y;
    private List<String> edges;

    public Node(String ID, int x, int y) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    public String getId() {
        return ID;
    }

    public List<String> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public void setEdges(List<String> edges) {
        this.edges = edges;
    }

    public void addEdge(String edge) {
        this.edges.add(edge);
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public Node clone() {
        Node node = new Node(ID, this.x, this.y);

        node.edges = this.edges;

        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(ID, node.ID) &&
                Objects.equals(x, node.x) &&
                Objects.equals(y, node.y) &&
                Objects.equals(edges, node.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, x, y, edges);
    }
}
