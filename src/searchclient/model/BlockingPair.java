package searchclient.model;

public class BlockingPair {
    private Node blockedNode;
    private Node blockedElement;

    public BlockingPair(Node blockedNode, Node blockedElement) {
        this.blockedNode = blockedNode;
        this.blockedElement = blockedElement;
    }

    public Node getBlockedNode() {
        return blockedNode;
    }

    public void setBlockedNode(Node blockedNode) {
        this.blockedNode = blockedNode;
    }

    public Node getBlockedElement() {
        return blockedElement;
    }

    public void setBlockedElement(Node blockedElement) {
        this.blockedElement = blockedElement;
    }
}
