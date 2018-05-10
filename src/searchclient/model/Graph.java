package searchclient.model;

import searchclient.Command;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.ColoredElement;
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
    private Priority priority;
    private int h;

    private int _hash = 0;

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

    public List<Goal> getPriorityGoalNodes() {
        List<Goal> priorityGoals = new ArrayList<>();
        for (String s : priority.getIDs()) {
            if (this.goals.containsKey(s)) {
                priorityGoals.add(this.goals.get(s));
            }
        }
        return priorityGoals;
    }

    public List<Box> getPriorityBoxNodes() {
        List<Box> priorityBoxes = new ArrayList<>();
            for (Goal g : getPriorityGoalNodes()) {
                for(Box b : this.boxes.values()){
                    if(b.getDesignatedGoal().equals(g.getID())){
                        priorityBoxes.add(b);
                    }
                }
            }
        return priorityBoxes;
    }

    public List<Agent> getPriorityAgents() {
        List<Agent> priorityAgents = new ArrayList<>();
        for (Box b : getPriorityBoxNodes()) {
            for (Agent a : this.agents.values()) {
                if (a.getColor().equals(b.getColor())) {
                    priorityAgents.add(a);
                }
            }
        }
        return priorityAgents;
    }


    public Map<String, Node> getAllNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

//    public List<Node> getAgentNodes() {
//        List<Node> result = new ArrayList<>();
//        this.agents.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
//        return Collections.unmodifiableList(result);
//    }
//
//    public List<Node> getGoalNodes() {
//        List<Node> result = new ArrayList<>();
//        this.goals.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
//        return Collections.unmodifiableList(result);
//    }
//
//    public List<Node> getBoxNodes() {
//        List<Node> result = new ArrayList<>();
//        this.boxes.values().forEach(a -> result.add(this.allNodes.get(a.getNodeID())));
//        return result;
//    }

    public void moveAgent(Agent originalAgent, Node toNode) {
        if (!this.agents.containsKey(originalAgent.getID())) {
            return;
        }
        Agent agent = this.agents.remove(originalAgent.getID());
        agent.setNode(toNode);
        this.agents.put(agent.getID(), agent);
    }

    public void moveBox(Box originalBox, Node toNode) {
        if (!this.boxes.containsKey(originalBox.getID())) {
            return;
        }
        Box box = this.boxes.remove(originalBox.getID());
        box.setNode(toNode);
        this.boxes.put(box.getID(), box);
    }

//    public Agent getAgent(Node node) {
//        return this.agents.get(node.getId());
//    }
//
//    public Box getBox(Node node) {
//        return this.boxes.get(node.getId());
//    }
//
//    public Goal getGoal(Node node) {
//        return this.goals.get(node.getId());
//    }

    public Graph getParent() {
        return parent;
    }

    public int g() {
        return g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

//    public boolean isGoalState() {
//        List<Node> goalNodes = this.getGoalNodes();
//        List<Node> boxNodes = this.getBoxNodes();
//        for (Node goal : goalNodes) {
//            if (!boxNodes.contains(goal)) {
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean isSubGoalState() {
        List<Goal> goals = this.getPriorityGoalNodes();
        List<Box> boxes = this.getPriorityBoxNodes();
        for (Goal goal : goals) {
            boolean foundMatch = false;
            for (Box box : boxes) {
                if (goal.hasSameNode(box) && goal.hasSameLetter(box)) {
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                return  false;
            }
        }
        return true;
    }

    public boolean isBoxAtGoal(Box box){
        Goal goal = this.goals.get(box.getDesignatedGoal());
        return goal.hasSameNode(box) && goal.hasSameColor(box);
    }

    public List<Graph> getExpandedStates() {
        List<Graph> expandedStates = new ArrayList<>();
        for (Agent agent : this.agents.values()) {
            Node agentNode = agent.getNode();
            for (String edge : agentNode.getEdges()) {
                Node newAgentNode = this.allNodes.get(edge);
                if (canBeMovedTo(newAgentNode)) {
                    expandedStates.add(this.newMoveAgentGraph(agent, newAgentNode));
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

    private Graph newMoveAgentGraph(Agent agent, Node newAgentNode) {
        Command command = new Command(getDir(agent.getNode(), newAgentNode));
        Graph graph = this.childState();
        graph.actions[Character.getNumericValue(agent.getLetter())] = command;
        graph.moveAgent(agent, newAgentNode);
        return graph;
    }

    private Optional<Graph> newPushBoxGraph(Agent agent, Box box, Node newAgentNode, Node newBoxNode) {
        Command command = new Command(Command.Type.Push, getDir(agent.getNode(), newAgentNode),
                getDir(newAgentNode, newBoxNode));
        if (!Command.isOpposite(command.dir1, command.dir2)) {
            Graph graph = this.childState();
            graph.actions[Character.getNumericValue(agent.getLetter())] = command;
            graph.moveAgent(agent, newAgentNode);
            graph.moveBox(box, newBoxNode);
            return Optional.of(graph);
        }
        return Optional.empty();
    }

    private Graph newPullBoxGraph(Box box, Agent agent, Node newAgentNode) {
        Command command = new Command(Command.Type.Pull, getDir(agent.getNode(), newAgentNode),
                getDir(agent.getNode(), box.getNode()));
        if (command.dir1.equals(Command.Dir.E) && command.dir2.equals(Command.Dir.E)) {
            System.out.println();
        }
        Graph graph = this.childState();
        graph.actions[Character.getNumericValue(agent.getLetter())] = command;
        graph.moveAgent(agent, newAgentNode);
        graph.moveBox(box, agent.getNode());
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

//    public Graph mergeGraphs(Graph graph) {
//        Graph newGraph = this.childState();
//        newGraph.parent = this.parent;
//        newGraph.actions = mergeActions(graph);
//        newGraph.moveAgent(oldAgentNode(graph), newAgentNode(graph));
//        return newGraph;
//    }
//
//    public boolean canBeMerged(Graph graph) {
//        if (!this.parent.equals(graph.parent)) {
//            return false;
//        }
//        for (int i = 0; i < this.actions.length; i++) {
//            if ((this.actions[i].isNoOp() && graph.actions[i].isNoOp()) ||
//                    (!this.actions[i].isNoOp()) && !graph.actions[i].isNoOp()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private Command[] mergeActions(Graph graph) {
//        Command[] newActions = new Command[this.actions.length];
//        for (int i = 0; i < this.actions.length; i++) {
//            if (!this.actions[i].isNoOp()) {
//                newActions[i] = this.actions[i];
//            } else if (!graph.actions[i].isNoOp()) {
//                newActions[i] = graph.actions[i];
//            }
//        }
//        return newActions;
//    }
//
//    private Node newAgentNode(Graph graph) {
//        for (int i = 0; i < graph.actions.length; i++) {
//            if (!graph.actions[i].isNoOp()) {
//                List<Node> nodes = graph.getAgentNodes();
//                for (Node node : nodes) {
//                    if (graph.getAgent(node).getLetter() == Character.getNumericValue(i)) {
//                        return node;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private Node oldAgentNode(Graph graph) {
//        for (int i = 0; i < graph.actions.length; i++) {
//            if (!graph.actions[i].isNoOp()) {
//                List<Node> nodes = graph.parent.getAgentNodes();
//                for (Node node : nodes) {
//                    if (graph.parent.getAgent(node).getLetter() == Character.getNumericValue(i)) {
//                        return node;
//                    }
//                }
//            }
//        }
//        return null;
//    }

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
            agentClone.put(agent.getID(), agent.clone());
        }
        Map<String, Box> boxClone = new HashMap<>();
        for (Box box : this.boxes.values()) {
            boxClone.put(box.getID(), box.clone());
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
        if (this._hash == 0) {
            this._hash = Objects.hash(getAgentNodes(), getBoxNodes());
        }
        return this._hash;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Node getDesignatedGoal(Node n){
        Box box = getBox(n);
        if (box != null) {
            return this.allNodes.get(box.getDesignatedGoal());
        } else {
            System.out.println();
        }
        return null;
    }

    public Node getAgentsCurrentBox(Node n ){
        return this.allNodes.get(getAgent(n).getCurrentBoxID());
    }
}
