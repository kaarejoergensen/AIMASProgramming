package searchclient.model;

import java.util.Objects;

public class Node {
    private String id;
    private char type;
    private int x;
    private int y;

    public Node(String id, char type, int x, int y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public char getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
