package searchclient.model;

import searchclient.model.Elements.ColeredElement;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int x;
    private int y;
    private List<ColeredElement> elements;

    public Node(int x, int y, ColeredElement element) {
        this.x = x;
        this.y = y;
        this.elements = new ArrayList<>();
        this.elements.add(element);
    }

    public Node(int x, int y, List<ColeredElement> elements) {
        this.x = x;
        this.y = y;
        this.elements = elements;
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.elements = new ArrayList<>();
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
