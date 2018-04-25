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
    private int rows;
    private int columns;
    private List<Node> allNodes;
    private List<Node> agentNodes;
    private List<Node> goalNodes;
    private List<Node> boxNodes;
    private int g;
    private Command[] actions;


    public Graph(Graph parent, int rows, int columns, List<Node> nodes) {
        this.parent = parent;
        this.rows = rows;
        this.columns = columns;
        this.allNodes = nodes;
        this.agentNodes = nodes.stream().filter(n -> n.getAgent() != null).collect(Collectors.toList());
        this.goalNodes = nodes.stream().filter(n -> n.getGoal() != null).collect(Collectors.toList());
        this.boxNodes = nodes.stream().filter(n -> n.getBox() != null).collect(Collectors.toList());
        this.actions = new Command[this.agentNodes.size()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new Command();
        }
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

    public boolean moveAgent(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        int fromIndex = this.allNodes.indexOf(fromNodeOriginal);
        if (fromIndex < 0) {
            System.out.println();
        }
        int toIndex = this.allNodes.indexOf(toNodeOriginal);
        if (toIndex < 0) {
            System.out.println();
        }
        Node fromNode = this.allNodes.get(this.allNodes.indexOf(fromNodeOriginal));
        Node toNode = this.allNodes.get(this.allNodes.indexOf(toNodeOriginal));
        if (fromNode == null || toNode == null) {
            throw new Exception("This should never happen");
        }
        boolean success;
        if (!fromNode.equals(fromNodeOriginal) || !toNode.equals(toNodeOriginal)) {
            System.out.println("");
        }
        toNode.setAgent(fromNode.getAgent());
        fromNode.setAgent(null);
        success = this.agentNodes.remove(fromNode);
        success &= this.agentNodes.add(toNode);
        if (!success) {
            throw new Exception("This should never happen");
        }
        return success;
    }

    public boolean moveBox(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        Node fromNode = this.allNodes.get(this.allNodes.indexOf(fromNodeOriginal));
        Node toNode = this.allNodes.get(this.allNodes.indexOf(toNodeOriginal));
        if (fromNode == null || toNode == null) {
            throw new Exception("This should never happen");
        }
        boolean success;
        toNode.setBox(fromNode.getBox());
        fromNode.setBox(null);
        success = this.boxNodes.remove(fromNode);
        if (!success) {
            System.out.println(this.toString());
            throw new Exception("This should never happen");
        }
        success &= this.boxNodes.add(toNode);
        if (!success) {
            System.out.println(this.toString());
            throw new Exception("This should never happen");
        }
        return  success;
    }

    public Graph getParent() {
        return parent;
    }

    public int g() {
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

    public List<Graph> getExpandedStates() throws Exception {
        List<Graph> expandedStates = new ArrayList<>();
        for (Node agentNode : this.agentNodes) {
            for (Edge edge : agentNode.getEdges()) {
                Node newAgentNode = edge.getDestination();
                if (newAgentNode.canBeMovedTo()) {
                    Command command = new Command(getDir(agentNode, newAgentNode));
                    Graph graph = this.childState();
                    graph.actions[Character.getNumericValue(agentNode.getAgent().getLetter())] = command;
                    graph.moveAgent(agentNode, newAgentNode);
                    expandedStates.add(graph);
                } else if (newAgentNode.getBox() != null &&
                        newAgentNode.getBox().getColor().equals(agentNode.getAgent().getColor()) && false) {
                    for (Edge newAgentNodeEdge : newAgentNode.getEdges()) {
                        Node newBoxNode = newAgentNodeEdge.getDestination();
                        if (newBoxNode.canBeMovedTo()) {
                            Command command = new Command(Command.Type.Push, getDir(agentNode, newAgentNode),
                                    getDir(newAgentNode, newBoxNode));
                            if (!Command.isOpposite(command.dir1, command.dir2)) {
                                Graph graph = this.childState();
                                graph.actions[Character.getNumericValue(agentNode.getAgent().getLetter())] = command;
                                graph.moveAgent(agentNode, newAgentNode);
                                graph.moveBox(newAgentNode, newBoxNode);
                                expandedStates.add(graph);
                            }
                        }
                    }
                    for (Edge newAgentNodeEdge : agentNode.getEdges()) {
                        Node newAgentNode1 = newAgentNodeEdge.getDestination();
                        if (newAgentNode1.canBeMovedTo()) {
                            Command command = new Command(Command.Type.Pull, getDir(agentNode, newAgentNode1),
                                    getDir(newAgentNode, agentNode));
                            Graph graph = this.childState();
                            graph.actions[Character.getNumericValue(agentNode.getAgent().getLetter())] = command;
                            graph.moveAgent(agentNode, newAgentNode1);
                            graph.moveBox(newAgentNode, agentNode);
                            expandedStates.add(graph);
                        }
                    }
                }
            }
        }
        return expandedStates;
    }

    private Command.Dir getDir(Node fromNode, Node toNode) {
        int newX = toNode.getX().compareTo(fromNode.getX());
        int newY = toNode.getY().compareTo(fromNode.getY());

        if (newX == 0 && newY < 0) {
            return Command.Dir.N;
        } else if (newX == 0 && newY > 0) {
            return Command.Dir.S;
        } else if (newX > 0 && newY == 0) {
            return Command.Dir.E;
        } else if (newX < 0 && newY == 0) {
            return Command.Dir.W;
        } else {
            return null;
        }
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
        return new Graph(this, this.rows, this.columns, clone);
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

    public Command[] getActions() {
        return actions;
    }

    public String actionsToString() {
        StringBuilder act = new StringBuilder("[");
        for (Command cmd : this.actions) {
            act.append(cmd).append(",");
        }
        act.setLength(act.length() - 1);
        act.append("]");
        return act.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.columns; col++) {
                int finalRow = row;
                int finalCol = col;
                Optional<Node> node = this.allNodes.stream().filter(n -> n.getY() == finalRow && n.getX() == finalCol).findFirst();
                if (node.isPresent()) {
                    if (node.get().getBox() != null) {
                        stringBuilder.append(node.get().getBox().getLetter());
                    } else if (node.get().getGoal() != null) {
                        stringBuilder.append(node.get().getGoal().getLetter());
                    } else if (node.get().getAgent() != null) {
                        stringBuilder.append(node.get().getAgent().getLetter());
                    } else {
                        stringBuilder.append(' ');
                    }
                } else {
                    stringBuilder.append('+');
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return  Objects.equals(allNodes, graph.allNodes) &&
                Objects.equals(agentNodes, graph.agentNodes) &&
                Objects.equals(goalNodes, graph.goalNodes) &&
                Objects.equals(boxNodes, graph.boxNodes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(allNodes, agentNodes, goalNodes, boxNodes);
    }
}
