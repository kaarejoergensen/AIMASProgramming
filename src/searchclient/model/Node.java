package searchclient.model;

import searchclient.model.Elements.ColeredElement;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<ColeredElement> elements;
    private List<Node> neighbours;

    public Node(ColeredElement element) {
        this.elements = new ArrayList<>();
        this.elements.add(element);
        this.neighbours = new ArrayList<>();
    }

    public Node(List<ColeredElement> elements) {
        this.elements = elements;
        this.neighbours = new ArrayList<>();
    }

    public Node() {
        this.elements = new ArrayList<>();
        this.neighbours = new ArrayList<>();
    }

    public List<ColeredElement> getElements() {
        return elements;
    }

    public void setElements(List<ColeredElement> elements) {
        this.elements = elements;
    }

    public List<Node> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Node> neighbours) {
        this.neighbours = neighbours;
    }
}
