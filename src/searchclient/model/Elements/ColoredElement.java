package searchclient.model.Elements;

import searchclient.model.Node;

import java.util.Objects;

public class ColoredElement {
    private String ID;
    private Node node;

    private char letter;
    private String color;

    public ColoredElement(String ID, Node node, char letter, String color) {
        this.ID = ID;
        this.node = node;
        this.letter = letter;
        this.color = color;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean hasSameLetter(ColoredElement other) {
        if (other == null)
            return false;
        return Character.toLowerCase(this.letter) == Character.toLowerCase(other.getLetter());
    }

    public boolean hasSameColor(ColoredElement other) {
        if (other == null)
            return false;
        return this.color.equals(other.getColor());
    }

    public boolean hasSameNode(ColoredElement other) {
        if (other == null)
            return false;
        return this.node.equals(other.getNode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColoredElement that = (ColoredElement) o;
        return letter == that.letter &&
                Objects.equals(color, that.color) &&
                Objects.equals(ID, that.ID) &&
                Objects.equals(node.getId(), that.node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, color, ID, node.getId());
    }
}
