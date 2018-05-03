package searchclient.model;

import searchclient.Command;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private Graph parent;
    private int rows;
    private int columns;
    private Map<String, Node> allNodes;
    private List<String> agentIDs;
    private List<String> boxIDs;
    private List<String> goalIDs;
    private int g;
    private Command[] actions;
    private int h;

    public Graph(Graph parent, int rows, int columns, Map<String, Node> allNodes) {
        this(parent, rows, columns, allNodes, null, null, null);
    }

    public Graph(Graph parent, int rows, int columns, Map<String, Node> nodes,
                 List<String> agentIDs, List<String> boxIDs, List<String> goalIDs) {
        this.parent = parent;
        this.rows = rows;
        this.columns = columns;
        this.allNodes = nodes;
        this.agentIDs = agentIDs == null ? this.allNodes.values().stream().
                filter(n -> n.getAgent() != null).map(Node::getId).collect(Collectors.toList()) : agentIDs;
        this.boxIDs = boxIDs == null ? this.allNodes.values().stream().
                filter(n -> n.getBox() != null).map(Node::getId).collect(Collectors.toList()) : boxIDs;
        this.goalIDs = goalIDs == null ? this.allNodes.values().stream().
                filter(n -> n.getGoal() != null).map(Node::getId).collect(Collectors.toList()) : goalIDs;
        this.actions = new Command[this.getAgentNodes().size()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new Command();
        }
        if (this.parent == null) {
            this.g = 0;
        } else {
            this.g = this.parent.g + 1;
        }
        this.h = -1;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Map<String, Node> getAllNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public List<Node> getAgentNodes() {
        List<Node> newList = new ArrayList<>();
        agentIDs.forEach(s -> newList.add(this.allNodes.get(s)));
        return Collections.unmodifiableList(newList);
    }

    public List<Node> getGoalNodes() {
        List<Node> newList = new ArrayList<>();
        goalIDs.forEach(s -> newList.add(this.allNodes.get(s)));
        return Collections.unmodifiableList(newList);
    }

    public List<Node> getBoxNodes() {
        List<Node> newList = new ArrayList<>();
        boxIDs.forEach(s -> newList.add(this.allNodes.get(s)));
        return Collections.unmodifiableList(newList);
    }

    public void moveAgent(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        Node fromNode = this.allNodes.get(fromNodeOriginal.getId());
        Node toNode = this.allNodes.get(toNodeOriginal.getId());
        toNode.setAgent(fromNode.getAgent());
        fromNode.setAgent(null);
        this.agentIDs.remove(fromNode.getId());
        this.agentIDs.add(toNode.getId());
    }

    public void moveBox(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        Node fromNode = this.allNodes.get(fromNodeOriginal.getId());
        Node toNode = this.allNodes.get(toNodeOriginal.getId());
        toNode.setBox(fromNode.getBox());
        fromNode.setBox(null);
        this.boxIDs.remove(fromNode.getId());
        this.boxIDs.add(toNode.getId());
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
        List<Node> goalNodes = this.getGoalNodes();
        List<Node> boxNodes = this.getBoxNodes();
        for (Node goal : goalNodes) {
            if (!boxNodes.contains(goal)) {
                return false;
            }
        }
        return true;
    }

    public List<Graph> getExpandedStates() throws Exception {
        List<Graph> expandedStates = new ArrayList<>();
        for (Node agentNode : this.getAgentNodes()) {
            for (String edge : agentNode.getEdges()) {
                Node newAgentNode = this.allNodes.get(edge);
                if (newAgentNode.canBeMovedTo()) {
                    Command command = new Command(getDir(agentNode, newAgentNode));
                    Graph graph = this.childState();
                    graph.actions[Character.getNumericValue(agentNode.getAgent().getLetter())] = command;
                    graph.moveAgent(agentNode, newAgentNode);
                    expandedStates.add(graph);
                } else if (newAgentNode.getBox() != null &&
                        newAgentNode.getBox().getColor().equals(agentNode.getAgent().getColor())) {
                    for (String newAgentNodeEdge : newAgentNode.getEdges()) {
                        Node newBoxNode = this.allNodes.get(newAgentNodeEdge);
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
                    for (String newAgentNodeEdge : agentNode.getEdges()) {
                        Node newAgentNode1 = this.allNodes.get(newAgentNodeEdge);
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
        Map<String, Node> allClone = new HashMap<>();
        for (Node n : this.allNodes.values()) {
            allClone.put(n.getId(), n.clone());
        }
        List<String> agentClone = new ArrayList<>(this.agentIDs);
        List<String> boxClone = new ArrayList<>(this.boxIDs);
        return new Graph(this, this.rows, this.columns, allClone,
                agentClone, boxClone, this.goalIDs);
    }

    public Optional<List<Node>> shortestPath(Node fromNode, Node toNode, boolean ignoreObstaclesOnPath) {
        if (fromNode == null || toNode == null || fromNode.equals(toNode)) {
            return Optional.of(new ArrayList<>());
        }
        List<String> visitedNodes = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Map<String, List<Node>> predecessors = new HashMap<>();

        visitedNodes.add(fromNode.getId());
        queue.add(fromNode);
        predecessors.put(fromNode.getId(), new ArrayList<>(Collections.singletonList(fromNode)));

        while (!queue.isEmpty()) {
            Node n = queue.poll();

            for (String edge : n.getEdges()) {
                Node destinationNode = this.allNodes.get(edge);
                if (destinationNode.equals(toNode)) {
                    List<Node> finalList = predecessors.get(n.getId());
                    finalList.add(toNode);
                    return Optional.of(finalList);
                }
                if (!visitedNodes.contains(edge) &&
                        (destinationNode.canBeMovedTo() || ignoreObstaclesOnPath)) {
                    queue.add(destinationNode);
                    visitedNodes.add(edge);
                    List<Node> predecessorList = new ArrayList<>();
                    if (predecessors.containsKey(n.getId())) {
                        predecessorList.addAll(predecessors.get(n.getId()));
                    }
                    predecessorList.add(destinationNode);
                    predecessors.put(edge, predecessorList);
                }
            }
        }
        return Optional.empty();
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
                Optional<Node> node = this.allNodes.values().stream().filter(n -> n.getY() == finalRow
                        && n.getX() == finalCol).findFirst();
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
        return Objects.equals(getAgentNodes(), graph.getAgentNodes()) &&
                Objects.equals(getBoxNodes(), graph.getBoxNodes()) &&
                Objects.equals(getGoalNodes(), graph.getGoalNodes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgentNodes(), getBoxNodes(), getGoalNodes());
    }
}
