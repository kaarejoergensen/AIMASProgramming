package searchclient.model;

import java.util.List;

public class Graph {
    private List<Node> nodes;
    private Graph parent;

    public Graph(Graph parent, List<Node> nodes) {
        this.parent = parent;
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Graph getParent() {
        return parent;
    }
}
