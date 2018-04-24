package searchclient.model;

import searchclient.Command;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.ColeredElement;
import searchclient.model.Elements.Goal;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private Graph parent;
    private List<Node> allNodes;
    private List<Node> agentNodes;
    private List<Node> goalNodes;
    private List<Node> boxNodes;
    private int g;
    private Command[] actions;


    public Graph(Graph parent, List<Node> nodes) {
        this.parent = parent;
        this.allNodes = nodes;
        this.agentNodes = nodes.stream().filter(n -> n.getElements().stream().
                anyMatch(e -> e instanceof Agent)).collect(Collectors.toList());
        this.goalNodes = nodes.stream().filter(n -> n.getElements().stream().
                anyMatch(e -> e instanceof Goal)).collect(Collectors.toList());
        this.boxNodes = nodes.stream().filter(n -> n.getElements().stream().
                anyMatch(e -> e instanceof Box)).collect(Collectors.toList());
        this.actions = new Command[(int) nodes.stream().filter(n -> n.getElements().stream().anyMatch(e -> e instanceof Agent)).count()];
        if (this.parent == null) {
            this.g = 0;
        } else {
            this.g = this.parent.g + 1;
        }
    }

    public List<Node> getAllNodes() {
        return Collections.unmodifiableList(allNodes);
    }

    public List<Node> getAgentNodes() {
        return Collections.unmodifiableList(agentNodes);
    }

    public List<Node> getGoalNodes() {
        return Collections.unmodifiableList(goalNodes);
    }

    public List<Node> getBoxNodes() {
        return Collections.unmodifiableList(boxNodes);
    }

    public boolean moveAgent(Node fromNode, Node toNode) {
        List<ColeredElement> agent = fromNode.getElements().stream().
                filter(e -> e instanceof Agent).collect(Collectors.toList());
        boolean success = false;
        if (agent.size() == 1) {
            success = toNode.addElement(agent.get(0));
            success &= fromNode.removeElement(agent.get(0));
            success &= this.agentNodes.remove(fromNode);
            success &= this.agentNodes.add(toNode);
        }
        return success;
    }

    public boolean moveBox(Node fromNode, Node toNode) {
        List<ColeredElement> box = fromNode.getElements().stream().
                filter(e -> e instanceof Box).collect(Collectors.toList());
        boolean success = false;
        if (box.size() == 1) {
            success = toNode.removeElement(box.get(0));
            success &= fromNode.addElement(box.get(0));
            success &= this.boxNodes.remove(fromNode);
            success &= this.boxNodes.add(toNode);
        }
        return  success;
    }

    public Graph getParent() {
        return parent;
    }

    public int getG() {
        return g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        for (Node goal : this.goalNodes) {
            if (!this.boxNodes.contains(goal)) {
                return false;
            }
        }
        return true;
    }

    public List<Graph> extractPlan() {
        LinkedList<Graph> plan = new LinkedList<>();
        Graph n = this;
        while (!n.isInitialState()) {
            plan.addFirst(n);
            n = n.parent;
        }
        return plan;
    }

    public Graph childState() {
        List<Node> clone = new ArrayList<>();
        for (Node n : this.allNodes) {
            clone.add(n.clone());
        }
        return new Graph(this, clone);
    }

    public List<Node> shortestPath(Node fromNode, Node toNode) {
        if (fromNode == null || toNode == null || fromNode.equals(toNode)) {
            return new ArrayList<>();
        }
        LinkedList<Node> result = new LinkedList<>();

        List<Node> visitedNodes = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Stack<Node> pathStack = new Stack<>();


        visitedNodes.add(fromNode);
        queue.add(fromNode);

        while (!queue.isEmpty()) {
            Node n = queue.poll();
            if (n.equals(toNode)) {
                break;
            }

            List<Edge> edges = n.getEdges();
            for (Edge edge : edges) {
                if (!visitedNodes.contains(edge.getDestination())) {
                    queue.add(edge.getDestination());
                    visitedNodes.add(edge.getDestination());
                    pathStack.add(edge.getDestination());
                }
            }
        }

        Node node, currentSrc = toNode;
        while(!pathStack.isEmpty()) {
            node = pathStack.pop();
            if (isNeighbours(currentSrc, node)) {
                result.addFirst(node);
                currentSrc = node;
                if (node == fromNode) {
                    break;
                }
            }
        }
        return result;
    }

    private boolean isNeighbours(Node fromNode, Node toNode) {
        return fromNode.getEdges().stream().anyMatch(e -> e.getDestination().equals(toNode))
                || toNode.getEdges().stream().anyMatch(e -> e.getDestination().equals(fromNode));
    }
}
