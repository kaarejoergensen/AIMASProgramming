package searchclient.model;

public class Edge {
    private String id;
    private Node source;
    private Node destination;

    public Edge(String id, Node source, Node destination) {
        this.id = id;
        this.source = source;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public int getWeight() {
        return 1;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id='" + id + '\'' +
                ", source=" + source +
                ", destination=" + destination +
                '}';
    }
}
