package searchclient.model;

import java.util.LinkedList;
import java.util.List;

public class NodePathPair {
    private Node node;
    private LinkedList<Node> path;

    public NodePathPair(Node node, LinkedList<Node> path) {
        this.node = node;
        this.path = path;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public LinkedList<Node> getPath() {
        return path;
    }

    public void setPath(LinkedList<Node> path) {
        this.path = path;
    }
}
