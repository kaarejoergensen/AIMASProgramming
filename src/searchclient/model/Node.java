package searchclient.model;

import searchclient.model.Elements.ColeredElement;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private List<ColeredElement> elements;
    private List<Edge> edges;

    public Node(String id, ColeredElement element) {
        this.id = id;
        this.elements = new ArrayList<>();
        this.elements.add(element);
        this.edges = new ArrayList<>();
    }

    public Node(String id, List<ColeredElement> elements) {
        this.id = id;
        this.elements = elements;
        this.edges = new ArrayList<>();
    }

    public Node(String id) {
        this.id = id;
        this.elements = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public List<ColeredElement> getElements() {
        return elements;
    }

    public void addElement(ColeredElement element) {
        if (!elements.contains(element)) {
            elements.add(element);
        }
    }

    public void removeElement(ColeredElement element) {
        elements.remove(element);
    }

    public String getId() {
        return id;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
