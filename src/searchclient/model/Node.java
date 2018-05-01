package searchclient.model;

import searchclient.model.Elements.ColeredElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private Integer x;
    private Integer y;
    private List<String> edges;

    public Node(int x, int y, ColeredElement element) {
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    public String getPositionAsString() {
        return String.valueOf(x) + ',' + String.valueOf(y);
    }

    public List<String> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public void addEdge(String edge) {
        this.edges.add(edge);
    }

    public void setEdges(List<String> edges) {
        this.edges = edges;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(x, node.x) &&
                Objects.equals(y, node.y) &&
                Objects.equals(edges, node.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, edges);
    }
}
