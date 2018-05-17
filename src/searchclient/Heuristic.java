package searchclient;

import searchclient.model.Elements.Box;
import searchclient.model.Graph;
import searchclient.model.Node;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Heuristic implements Comparator<Graph> {

    public Heuristic() {
    }

    public int h(Graph graph) {

        if (graph.getH() != -1) {
            return graph.getH();
        }
        int result = 0;

        List<Node> currentBoxes = new ArrayList<>();

        result += rewardAgentToBox(currentBoxes, graph);
        result += rewardBoxToGoal(currentBoxes, graph);

        graph.setH(result);
        return result;
    }

    public abstract int f(Graph n);

    private int rewardAgentToBox(List<Node> currentBoxes, Graph graph){
        int result = 0;

        for (Node agentNode : graph.getPriorityAgents()) {
            Node currentBox = graph.getAgentsCurrentBox(agentNode);
            Box tmpBox = graph.getBox(currentBox);
            if(tmpBox.checkIfPathIsClear(graph)){
                List<Node> path = tmpBox.getPathToGoal();
                if(tmpBox.getPathToGoal() != null){
                    result += path.size();
                }else {
                    result += 110;
                }
            }else{
                Node blockingBox = null;
                for(Node n : tmpBox.getPathToGoal()){
                    if(graph.getBox(n) != null && !graph.getBox(n).getBoxID().equals(currentBox.getId()) && graph.getBox(n).hasSameColor(graph.getAgent(agentNode))){
                        blockingBox = n;
                        break;
                    }
                }
                result += graph.shortestPath(agentNode, blockingBox, true, graph.getAgent(agentNode)).map(List::size).orElse(200);

                tmpBox.updatePathToGoal(graph);
            }
            currentBoxes.add(currentBox);
        }
        return result;
    }

    private int rewardBoxToGoal(List<Node> currentBoxes, Graph graph){
        int result = 0;

        //Clear path - get all non current boxes
        List<Node> notCurrentBoxes = graph.getBoxNodes().stream().filter(p -> !currentBoxes.contains(p)).collect(Collectors.toList());

        for (Node boxNode : currentBoxes) {
            Node goal = graph.getDesignatedGoal(boxNode);
            Box tempBox = graph.getBox(boxNode);
            if (!goal.equals(boxNode)) {
                if(tempBox.checkIfPathIsClear(graph)){
                    Optional<List<Node>> path = graph.shortestPath(boxNode, goal, true, null);
                    result += path.map(p -> 3 * p.size()).orElse(100);
                }else{
                    for (Node bn : notCurrentBoxes) {
                        if(tempBox.getPathToGoal().contains(bn)) {
                            result += 100;
                        }
                    }

                }

            }
        }
        return result;
    }


    @Override
    public int compare(Graph n1, Graph n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {

        public AStar() {
            super();
        }

        @Override
        public int f(Graph n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(int W) {
            super();
            this.W = W;
        }

        @Override
        public int f(Graph n) {
            return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
            return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {

        public Greedy() {
            super();
        }

        @Override
        public int f(Graph n) {
            return this.h(n);
        }

        @Override
        public String toString() {
            return "Greedy evaluation";
        }
    }
}
