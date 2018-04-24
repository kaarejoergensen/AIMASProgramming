package searchclient.model;

import searchclient.model.Elements.ColeredElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private int x;
    private int y;
    private List<ColeredElement> elements;
    private List<Edge> edges;

    public Node(int x, int y, ColeredElement element) {
        this.x = x;
        this.y = y;
        this.elements = new ArrayList<>();
        this.elements.add(element);
        this.edges = new ArrayList<>();
    }

    public Node(int x, int y, List<ColeredElement> elements) {
        this.x = x;
        this.y = y;
        this.elements = elements;
        this.edges = new ArrayList<>();
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.elements = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public List<ColeredElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public boolean addElement(ColeredElement element) {
        if (!elements.contains(element)) {
            return elements.add(element);
        }
        return false;
    }

    public boolean removeElement(ColeredElement element) {
        return elements.remove(element);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public Node clone() {
        Node node = new Node(this.x, this.y);

        node.elements = new ArrayList<>(this.elements);
        node.edges = this.edges;

        return node;
    }
}
