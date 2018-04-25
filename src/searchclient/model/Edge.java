package searchclient.model;

import java.util.Objects;

public class Edge {
    private Node source;
    private Node destination;

    public Edge(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(source.getId(), edge.source.getId()) &&
                Objects.equals(destination.getId(), edge.destination.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source.getId(), destination.getId());
    }
}
