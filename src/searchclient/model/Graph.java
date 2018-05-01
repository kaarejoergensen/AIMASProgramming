package searchclient.model;

import searchclient.Command;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.Goal;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private Graph parent;
    private int rows;
    private int columns;
    private Map<String, Node> allNodes;
    private Map<String, Agent> agents;
    private Map<String, Box> boxes;
    private Map<String, Goal> goals;
    private int g;
    private Command[] actions;


    public Graph(Graph parent, int rows, int columns, Map<String, Node> allNodes,
                 Map<String, Agent> agents, Map<String, Box> boxes, Map<String, Goal> goals) {
        this.parent = parent;
        this.rows = rows;
        this.columns = columns;
        this.allNodes = allNodes;
        this.agents = agents;
        this.boxes = boxes;
        this.goals = goals;
        this.actions = new Command[this.agents.size()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new Command();
        }
        if (this.parent == null) {
            this.g = 0;
        } else {
            this.g = this.parent.g + 1;
        }
    }

    public Map<String, Node> getAllNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public List<Node> getAgentNodes() {
        List<Node> result = new ArrayList<>();
        this.agents.values().forEach(a -> result.add(this.allNodes.get(a.getPositionAsString())));
        return Collections.unmodifiableList(result);
    }

    public List<Node> getGoalNodes() {
        List<Node> result = new ArrayList<>();
        this.goals.values().forEach(a -> result.add(this.allNodes.get(a.getPositionAsString())));
        return Collections.unmodifiableList(result);
    }

    public List<Node> getBoxNodes() {
        List<Node> result = new ArrayList<>();
        this.boxes.values().forEach(a -> result.add(this.allNodes.get(a.getPositionAsString())));
        return Collections.unmodifiableList(result);
    }

    public void moveAgent(Node fromNode, Node toNode) {
        if (!this.agents.containsKey(fromNode.getPositionAsString()) ||
                this.agents.containsKey(toNode.getPositionAsString())) {
            return;
        }
        Agent agent = this.agents.remove(fromNode.getPositionAsString());
        agent.setX(toNode.getX());
        agent.setY(toNode.getY());
        this.agents.put(agent.getPositionAsString(), agent);
    }

    public void moveBox(Node fromNode, Node toNode) {
        if (!this.boxes.containsKey(fromNode.getPositionAsString()) ||
                this.boxes.containsKey(toNode.getPositionAsString())) {
            return;
        }
        Box box = this.boxes.remove(fromNode.getPositionAsString());
        box.setX(toNode.getX());
        box.setY(toNode.getY());
        this.boxes.put(box.getPositionAsString(), box);
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
            for (String destination : agentNode.getEdges()) {
                Node newAgentNode = this.allNodes.get(destination);
                if (canBeMovedTo(newAgentNode)) {
                    Command command = new Command(getDir(agentNode, newAgentNode));
                    Graph graph = this.childState();
                    graph.actions[Character.getNumericValue(this.agents.get(agentNode.getPositionAsString()).getLetter())] = command;
                    graph.moveAgent(agentNode, newAgentNode);
                    expandedStates.add(graph);
                } else if (this.boxes.containsKey(newAgentNode.getPositionAsString()) &&
                        this.boxes.get(newAgentNode.getPositionAsString()).getColor().
                                equals(this.agents.get(agentNode.getPositionAsString()).getColor())) {
                    for (String newAgentNodeEdge : newAgentNode.getEdges()) {
                        Node newBoxNode = this.allNodes.get(newAgentNodeEdge);
                        if (canBeMovedTo(newBoxNode)) {
                            Command command = new Command(Command.Type.Push, getDir(agentNode, newAgentNode),
                                    getDir(newAgentNode, newBoxNode));
                            if (!Command.isOpposite(command.dir1, command.dir2)) {
                                Graph graph = this.childState();
                                graph.actions[Character.getNumericValue(this.agents.get(agentNode.getPositionAsString()).getLetter())] = command;
                                graph.moveAgent(agentNode, newAgentNode);
                                graph.moveBox(newAgentNode, newBoxNode);
                                expandedStates.add(graph);
                            }
                        }
                    }
                    for (String newAgentNodeEdge : agentNode.getEdges()) {
                        Node newAgentNode1 = this.allNodes.get(newAgentNodeEdge);
                        if (canBeMovedTo(newAgentNode1)) {
                            Command command = new Command(Command.Type.Pull, getDir(agentNode, newAgentNode1),
                                    getDir(newAgentNode, agentNode));
                            Graph graph = this.childState();
                            graph.actions[Character.getNumericValue(this.agents.get(agentNode.getPositionAsString()).getLetter())] = command;
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

    public boolean canBeMovedTo(Node node) {
        return !this.agents.containsKey(node.getPositionAsString()) && !this.boxes.containsKey(node.getPositionAsString());
    }

    public Agent getAgent(Node node) {
        return this.agents.get(node.getPositionAsString());
    }

    public Box getBox(Node node) {
        return this.boxes.get(node.getPositionAsString());
    }

    public Goal getGoal(Node node) {
        return this.goals.get(node.getPositionAsString());
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
        Map<String, Agent> agentClone = new HashMap<>();
        for (Agent agent : this.agents.values()) {
            agentClone.put(agent.getPositionAsString(), agent.clone());
        }
        Map<String, Box> boxClone = new HashMap<>();
        for (Box box : this.boxes.values()) {
            boxClone.put(box.getPositionAsString(), box.clone());
        }
        return new Graph(this, this.rows, this.columns, this.allNodes, agentClone, boxClone, this.goals);
    }

    public Optional<List<Node>> shortestPath(Node fromNode, Node toNode, boolean ignoreObstaclesOnPath) {
        if (fromNode == null || toNode == null || fromNode.equals(toNode)) {
            return Optional.of(new ArrayList<>());
        }
        List<String> visitedNodes = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Map<String, List<Node>> predecessors = new HashMap<>();

        visitedNodes.add(fromNode.getPositionAsString());
        queue.add(fromNode);
        predecessors.put(fromNode.getPositionAsString(), new ArrayList<>(Collections.singletonList(fromNode)));

        while (!queue.isEmpty()) {
            Node n = queue.poll();

            for (String edge : n.getEdges()) {
                Node destinationNode = this.allNodes.get(edge);
                if (destinationNode.equals(toNode)) {
                    List<Node> finalList = predecessors.get(n.getPositionAsString());
                    finalList.add(toNode);
                    return Optional.of(finalList);
                }
                if (!visitedNodes.contains(edge) &&
                        (canBeMovedTo(destinationNode) || ignoreObstaclesOnPath)) {
                    queue.add(destinationNode);
                    visitedNodes.add(edge);
                    List<Node> predecessorList = new ArrayList<>();
                    if (predecessors.containsKey(n.getPositionAsString())) {
                        predecessorList.addAll(predecessors.get(n.getPositionAsString()));
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
                    if (this.boxes.containsKey(node.get().getPositionAsString())) {
                        stringBuilder.append(this.boxes.get(node.get().getPositionAsString()).getLetter());
                    } else if (this.goals.containsKey(node.get().getPositionAsString())) {
                        stringBuilder.append(this.goals.get(node.get().getPositionAsString()).getLetter());
                    } else if (this.agents.containsKey(node.get().getPositionAsString())) {
                        stringBuilder.append(this.agents.get(node.get().getPositionAsString()).getLetter());
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
        return rows == graph.rows &&
                columns == graph.columns &&
                Objects.equals(allNodes, graph.allNodes) &&
                Objects.equals(agents, graph.agents) &&
                Objects.equals(boxes, graph.boxes) &&
                Objects.equals(goals, graph.goals);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rows, columns, allNodes, agents, boxes, goals);
    }
}
