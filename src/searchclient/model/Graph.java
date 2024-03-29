package searchclient.model;

import searchclient.Command;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.Goal;

import java.net.Inet4Address;
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
    private Priority priority;
    private int h;

    private int _hash = -1;

    public Graph(Graph parent, int rows, int columns, Map<String, Node> nodes,
                 Map<String, Agent> agents, Map<String, Box> boxes, Map<String, Goal> goals) {
        this.parent = parent;
        this.rows = rows;
        this.columns = columns;
        this.allNodes = nodes;
        this.agents = agents;
        this.boxes = boxes;
        this.goals = goals;
        this.actions = new Command[this.getAgentNodes().size()];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new Command();
        }
        if (this.parent == null) {
            this.g = 0;
        } else {
            this.g = this.parent.g + 1;

            if (this.parent.priority != null) {
                this.priority = this.parent.priority;
            }

        }
        this.h = -1;

    }



    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public List<Node> getPriorityGoalNodes() {
        List<Node> priorityGoals = new ArrayList<>();
        for (Node n : this.getGoalNodes()) {

            for (String s : priority.getIDs()) {
                if (n.getId().equals(s)) {
                    priorityGoals.add(n);
                }
            }
        }
        return priorityGoals;
    }

    public List<Node> getPriorityBoxNodes() {
        List<Node> priorityBoxes = new ArrayList<>();
            for (Node g : getPriorityGoalNodes()) {
                for(Node b : getBoxNodes()){
                    if(getBox(b).getDesignatedGoal() != null && getBox(b).getDesignatedGoal().equals(g.getId())){
                        priorityBoxes.add(b);
                    }
                }
            }
        return priorityBoxes;
    }

    public List<Node> getPriorityAgents() {
        List<Node> priorityAgents = new ArrayList<>();

        for (Node b : getPriorityBoxNodes()) {
            for (Node a : getAgentNodes()) {
                if (getAgent(a).getColor().equals(getBox(b).getColor())) {
                    priorityAgents.add(a);
                }
            }
        }
        return priorityAgents;
    }


    public Map<String, Node> getAllNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public List<Node> getAgentNodes() {
        List<Node> result = new ArrayList<>();
        this.agents.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
        return Collections.unmodifiableList(result);
    }

    public List<Node> getGoalNodes() {
        List<Node> result = new ArrayList<>();
        this.goals.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
        return Collections.unmodifiableList(result);
    }

    public List<Node> getBoxNodes() {
        List<Node> result = new ArrayList<>();
        this.boxes.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
        return result;
    }

    public void moveAgent(Node fromNode, Node toNode) {
        if (!this.agents.containsKey(fromNode.getId()) ||
                this.agents.containsKey(toNode.getId())) {
            return;
        }
        Agent agent = this.agents.remove(fromNode.getId());
        agent.setNodeID(toNode.getId());
        this.agents.put(agent.getNodeID(), agent);
    }

    public void moveBox(Node fromNode, Node toNode) {
        if (!this.boxes.containsKey(fromNode.getId()) ||
                this.boxes.containsKey(toNode.getId())) {
            return;
        }
        Box box = this.boxes.remove(fromNode.getId());
        box.setNodeID(toNode.getId());
        this.boxes.put(box.getNodeID(), box);
    }

    public Agent getAgent(Node node) {
        return this.agents.get(node.getId());
    }

    public Box getBox(Node node) {
        return this.boxes.get(node.getId());
    }

    public Goal getGoal(Node node) {
        return this.goals.get(node.getId());
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

    public boolean isSubGoalState() {
        List<Node> goalNodes = this.getPriorityGoalNodes();
        List<Node> boxNodes = this.getPriorityBoxNodes();
        return goalNodes.equals(boxNodes) && goalNodes.stream().allMatch(g -> getGoal(g).hasSameLetter(getBox(g)));
    }

    public boolean isBoxAtGoal(Node b){
        Node goal = getDesignatedGoal(b);
        return goal.equals(b) && getBox(b).getDesignatedGoal().equals(getGoal(goal).getNodeID());
    }

    public List<Graph> getExpandedStates() {
        List<Graph> expandedStates = new ArrayList<>();
        for (Node agentNode : this.getAgentNodes()) {
            for (String edge : agentNode.getEdges()) {
                Node newAgentNode = this.allNodes.get(edge);
                if (canBeMovedTo(newAgentNode)) {
                    expandedStates.add(this.newMoveAgentGraph(agentNode, newAgentNode));
                } else if (boxCanBeMoved(agentNode, newAgentNode)) {
                    for (String newAgentNodeEdge : newAgentNode.getEdges()) {
                        Node newBoxNode = this.allNodes.get(newAgentNodeEdge);
                        if (canBeMovedTo(newBoxNode)) {
                            newPushBoxGraph(agentNode, newAgentNode, newBoxNode).map(expandedStates::add);
                        }
                    }
                    for (String newAgentNodeEdge : agentNode.getEdges()) {
                        Node newAgentNode1 = this.allNodes.get(newAgentNodeEdge);
                        if (canBeMovedTo(newAgentNode1)) {
                            expandedStates.add(newPullBoxGraph(newAgentNode, agentNode, newAgentNode1));
                        }
                    }
                }
            }
        }

        return expandedStates;
    }

    private Graph newMoveAgentGraph(Node oldAgentNode, Node newAgentNode) {
        Command command = new Command(getDir(oldAgentNode, newAgentNode));
        Graph graph = this.childState();
        graph.actions[Character.getNumericValue(this.agents.get(oldAgentNode.getId()).getLetter())] = command;
        graph.moveAgent(oldAgentNode, newAgentNode);
        return graph;
    }

    private Optional<Graph> newPushBoxGraph(Node oldAgentNode, Node newAgentNode, Node newBoxNode) {
        Command command = new Command(Command.Type.Push, getDir(oldAgentNode, newAgentNode),
                getDir(newAgentNode, newBoxNode));
        if (!Command.isOpposite(command.dir1, command.dir2)) {
            command.boxID = this.getBox(newAgentNode).getBoxID();
            Graph graph = this.childState();
            graph.actions[Character.getNumericValue(this.agents.get(oldAgentNode.getId()).getLetter())] = command;
            graph.moveAgent(oldAgentNode, newAgentNode);
            graph.moveBox(newAgentNode, newBoxNode);
            return Optional.of(graph);
        }
        return Optional.empty();
    }

    private Graph newPullBoxGraph(Node oldBoxNode, Node oldAgentNode, Node newAgentNode) {
        Command command = new Command(Command.Type.Pull, getDir(oldAgentNode, newAgentNode),
                getDir(oldAgentNode, oldBoxNode));
        command.boxID = this.getBox(oldBoxNode).getBoxID();
        Graph graph = this.childState();
        graph.actions[Character.getNumericValue(this.agents.get(oldAgentNode.getId()).getLetter())] = command;
        graph.moveAgent(oldAgentNode, newAgentNode);
        graph.moveBox(oldBoxNode, oldAgentNode);
        return graph;
    }

    private boolean boxCanBeMoved(Node oldAgentNode, Node newAgentNode) {
        return this.boxes.get(newAgentNode.getId()) != null &&
                this.boxes.get(newAgentNode.getId()).getColor().equals(this.agents.get(oldAgentNode.getId()).getColor());
    }

    public boolean canBeMovedTo(Node node, Agent ignoreAgent) {
        return (!this.agents.containsKey(node.getId()) ||
                (ignoreAgent != null && this.agents.get(node.getId()).equals(ignoreAgent))) &&
                !this.boxes.containsKey(node.getId());
    }

    public boolean canBeMovedTo(Node node) {
        return !this.agents.containsKey(node.getId()) && !this.boxes.containsKey(node.getId());
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

    public Graph mergeGraphs(Graph graph) {
        Graph newGraph = this.childState();
        newGraph.parent = this.parent;
        newGraph.actions = mergeActions(graph);
        newGraph.moveAgent(oldAgentNode(graph), newAgentNode(graph));
        return newGraph;
    }

    public boolean canBeMerged(Graph graph) {
        if (!this.parent.equals(graph.parent)) {
            return false;
        }
        for (int i = 0; i < this.actions.length; i++) {
            if ((this.actions[i].isNoOp() && graph.actions[i].isNoOp()) ||
                    (!this.actions[i].isNoOp()) && !graph.actions[i].isNoOp()) {
                return false;
            }
        }

        return true;
    }

    private Command[] mergeActions(Graph graph) {
        Command[] newActions = new Command[this.actions.length];
        for (int i = 0; i < this.actions.length; i++) {
            if (!this.actions[i].isNoOp()) {
                newActions[i] = this.actions[i];
            } else if (!graph.actions[i].isNoOp()) {
                newActions[i] = graph.actions[i];
            }
        }
        return newActions;
    }

    private Node newAgentNode(Graph graph) {
        for (int i = 0; i < graph.actions.length; i++) {
            if (!graph.actions[i].isNoOp()) {
                List<Node> nodes = graph.getAgentNodes();
                for (Node node : nodes) {
                    if (graph.getAgent(node).getLetter() == Character.getNumericValue(i)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    private Node oldAgentNode(Graph graph) {
        for (int i = 0; i < graph.actions.length; i++) {
            if (!graph.actions[i].isNoOp()) {
                List<Node> nodes = graph.parent.getAgentNodes();
                for (Node node : nodes) {
                    if (graph.parent.getAgent(node).getLetter() == Character.getNumericValue(i)) {
                        return node;
                    }
                }
            }
        }
        return null;
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
            agentClone.put(agent.getNodeID(), agent.clone());
        }
        Map<String, Box> boxClone = new HashMap<>();
        for (Box box : this.boxes.values()) {
            boxClone.put(box.getNodeID(), box.clone());
        }
        return new Graph(this, this.rows, this.columns, this.allNodes, agentClone, boxClone, this.goals);
    }

    public Optional<List<Node>> shortestPath(Node fromNode, Node toNode, boolean ignoreObstaclesOnPath, Agent ignoreAgent) {
        if (fromNode == null || toNode == null || fromNode.equals(toNode)) {
            return Optional.of(new ArrayList<>());
        }
        if (fromNode.getEdges().contains(toNode.getId())) {
            return Optional.of(Collections.singletonList(fromNode));
        }
        if (getGoal(toNode) != null && !canBeMovedTo(toNode, ignoreAgent)) {
            return Optional.empty();
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
                    return Optional.of(finalList);
                }
                if (!visitedNodes.contains(edge) &&
                        (ignoreObstaclesOnPath || canBeMovedTo(destinationNode, ignoreAgent))) {
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
                    if (this.boxes.containsKey(node.get().getId())) {
                        stringBuilder.append(this.boxes.get(node.get().getId()).getLetter());
                    } else if (this.goals.containsKey(node.get().getId())) {
                        stringBuilder.append(this.goals.get(node.get().getId()).getLetter());
                    } else if (this.agents.containsKey(node.get().getId())) {
                        stringBuilder.append(this.agents.get(node.get().getId()).getLetter());
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
        return this.hashCode() == graph.hashCode();
    }

    @Override
    public int hashCode() {
        if (this._hash == -1) {
            this._hash = Objects.hash(getAgentNodes().toString(), getBoxNodes().toString());
        }
        return this._hash;
    }



    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public Node getDesignatedGoal(Node n){
        return this.getAllNodes().get(getBox(n).getDesignatedGoal());
    }

    public Node getAgentsCurrentBox(Node n ){
        for(Node g : getBoxNodes()){
            if(getAgent(n).getCurrentBoxID().equals(getBox(g).getBoxID()))
                return g;
        }
        return null;
    }


}
