package searchclient.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private Node[][] nodes;
    private Graph parent;

    public Graph(Graph parent, Node[][] nodes) {
        this.parent = parent;
        this.nodes = nodes;
    }

    public Node[][] getNodes() {
        return nodes;
    }

    public List<Node> getNeighbours(Node n) {
        List<Node> neighbours = new ArrayList<>();
        if (n == null) {
            return neighbours;
        }
        int x = n.getX();
        int y = n.getY();
        if (x > 0 && nodes[x - 1][y] != null) {
            neighbours.add(nodes[x - 1][y]);
        }
        if (x < nodes.length && nodes[x + 1][y] != null) {
            neighbours.add(nodes[x + 1][y]);
        }
        if (y > 0 && nodes[x][y - 1] != null) {
            neighbours.add(nodes[x][y - 1]);
        }
        if (y < nodes[x].length && nodes[x][y + 1] != null) {
            neighbours.add(nodes[x][y + 1]);
        }
        return neighbours;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < nodes.length; row++) {
            for (int col = 0; col < nodes[row].length; col++) {
                Node n = nodes[row][col];
                if (n == null) {
                    s.append("+");
                } else if (n.getElements().size() > 0) {
                    n.getElements().forEach(e -> s.append(e.getLetter()));
                } else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }
}
