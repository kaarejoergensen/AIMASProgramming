package searchclient.model;

import searchclient.Command;
import searchclient.exceptions.NoPathFoundException;
import searchclient.model.Elements.Agent;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private Graph parent;
    private int rows;
    private int columns;
    private Map<String, Node> allNodes;
    private int g;
    private Command[] actions;
    private Priority priority;


    public Graph(Graph parent, int rows, int columns, Map<String, Node> nodes) {
        this.parent = parent;
        this.rows = rows;
        this.columns = columns;
        this.allNodes = nodes;



        this.actions = new Command[this.getAgentNodes().size()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new Command();
        }
        if (this.parent == null) {
            this.g = 0;
        } else {
            this.g = this.parent.g + 1;
            if(this.parent.priority != null){
                this.priority = this.parent.priority;
            }
        }


    }

    public List<Node> getPrioirtyGoalNodes(){
        List<Node> tmp_goals = new ArrayList<>();
        for(Node n : this.getGoalNodes()){
            Character x = n.getGoal().getLetter();
            for(Character t : priority.getLetters()){
                if(Character.toLowerCase(x) == Character.toLowerCase(t)){
                    tmp_goals.add(n);
                }
            }
        }
        return tmp_goals;
    }

    public List<Node> getPriorityBoxNodes(){
        List<Node> tmp_boxes = new ArrayList<>();
        for(Node n : this.getBoxNodes()){
            Character x = n.getBox().getLetter();
            for(Character t : priority.getLetters()){
                if(Character.toLowerCase(x) == Character.toLowerCase(t)){
                    tmp_boxes.add(n);
                }
            }
        }
        return tmp_boxes;
    }

    public List<Node> getSpecificAgents(){
        List<Node> tmp_agents = new ArrayList<>();
        List<Node> relevantBoxes = getPriorityBoxNodes();
        for(Node b : relevantBoxes){
            for(Node a : getAgentNodes()){
                if(a.getAgent().getColor().equals(b.getBox().getColor())){
                    tmp_agents.add(a);
                }
            }
        }
        return tmp_agents;
    }

    public Map<String, Node> getAllNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public List<Node> getAgentNodes() {
        return Collections.unmodifiableList(this.allNodes.values().stream().filter(n -> n.getAgent() != null).
                collect(Collectors.toList()));
    }

    public List<Node> getGoalNodes() {
        return Collections.unmodifiableList(this.allNodes.values().stream().filter(n -> n.getGoal() != null).
                collect(Collectors.toList()));
    }

    public List<Node> getBoxNodes() {
        return Collections.unmodifiableList(this.allNodes.values().stream().filter(n -> n.getBox() != null).
                collect(Collectors.toList()));
    }

    private void moveAgent(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        Node fromNode = this.allNodes.get(fromNodeOriginal.getId());
        Node toNode = this.allNodes.get(toNodeOriginal.getId());
        toNode.setAgent(fromNode.getAgent());
        fromNode.setAgent(null);
    }

    private void moveBox(Node fromNodeOriginal, Node toNodeOriginal) throws Exception {
        Node fromNode = this.allNodes.get(fromNodeOriginal.getId());
        Node toNode = this.allNodes.get(toNodeOriginal.getId());
        toNode.setBox(fromNode.getBox());
        fromNode.setBox(null);
    }

    public Graph getParent() {
        return parent;
    }

    public int g() {
        return g;
    }

    private boolean isInitialState() {
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

    public boolean isSubGoalState(List<Node> goalNodes, List<Node> boxNodes){
        for (Node goal : goalNodes) {
            if (!boxNodes.contains(goal)) {
                return false;
            }
        }
        return true;
    }

    public List<Graph> getExpandedStates() throws Exception {

        List<Graph> expandedStates = new ArrayList<>();

        for (Node agentNode : this.getSpecificAgents()) {
            for (Edge edge : agentNode.getEdges()) {
                Node newAgentNode = this.allNodes.get(edge.getDestination());
                if (newAgentNode.canBeMovedTo()) {
                    Command command = new Command(getDir(agentNode, newAgentNode));
                    Graph graph = this.childState();
                    graph.actions[Character.getNumericValue(agentNode.getAgent().getLetter())] = command;
                    graph.moveAgent(agentNode, newAgentNode);
                    expandedStates.add(graph);
                } else if (newAgentNode.getBox() != null &&
                        newAgentNode.getBox().getColor().equals(agentNode.getAgent().getColor())) {
                    for (Edge newAgentNodeEdge : newAgentNode.getEdges()) {
                        Node newBoxNode = this.allNodes.get(newAgentNodeEdge.getDestination());
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
                        Node newAgentNode1 = this.allNodes.get(newAgentNodeEdge.getDestination());
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
        return new Graph(this, this.rows, this.columns, allClone);
    }

    public List<Node> shortestPath(Node fromNode, Node toNode) throws NoPathFoundException {
        if (fromNode == null || toNode == null || fromNode.equals(toNode)) {
            return new ArrayList<>();
        }
        List<String> visitedNodes = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Map<String, List<Node>> predecessors = new HashMap<>();

        visitedNodes.add(fromNode.getId());
        queue.add(fromNode);
        predecessors.put(fromNode.getId(), new ArrayList<>(Collections.singletonList(fromNode)));

        while (!queue.isEmpty()) {
            Node n = queue.poll();

            for (Edge edge : n.getEdges()) {
                Node destinationNode = this.allNodes.get(edge.getDestination());
                if (destinationNode.equals(toNode)) {
                    List<Node> finalList = predecessors.get(n.getId());
                    finalList.add(toNode);
                    return finalList;
                }
                if (!visitedNodes.contains(edge.getDestination()) &&
                        destinationNode.canBeMovedTo()) {
                    queue.add(destinationNode);
                    visitedNodes.add(edge.getDestination());
                    List<Node> predecessorList = new ArrayList<>();
                    if (predecessors.containsKey(n.getId())) {
                        predecessorList.addAll(predecessors.get(n.getId()));
                    }
                    predecessorList.add(destinationNode);
                    predecessors.put(edge.getDestination(), predecessorList);
                }
            }
        }
        throw new NoPathFoundException();
    }

    private boolean isNeighbours(Node fromNode, Node toNode) {
        return fromNode.getEdges().stream().anyMatch(e -> e.getDestination().equals(toNode.getId()))
                || toNode.getEdges().stream().anyMatch(e -> e.getDestination().equals(fromNode.getId()));
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
        return  Objects.equals(allNodes, graph.allNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allNodes);
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
