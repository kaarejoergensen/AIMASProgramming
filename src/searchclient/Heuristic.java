package searchclient;

import searchclient.model.Graph;
import searchclient.model.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Heuristic implements Comparator<Graph> {

    HashMap<Character, Integer> prioList;

    public Heuristic(Graph n, HashMap<Character, Integer> priortyList) {
        this.prioList = priortyList;
    }

    public int h(Graph graph) {

        int result = 0;

        

        for (Node agentNode : graph.getAgentNodes()) {
            List<Node> boxNodesWithSameColor = graph.getBoxNodes().stream().
                    filter(n -> n.getBox() != null && n.getBox().getColor().equals(agentNode.getAgent().getColor())).
                    collect(Collectors.toList());
            for (Node boxNode : boxNodesWithSameColor) {
                result += 3 * graph.shortestPath(agentNode, boxNode).size();
            }
        }
        for (Node boxNode : graph.getBoxNodes()) {
            List<Node> goalNodesWithSameLetter = graph.getGoalNodes().stream().
                    filter(n -> n.getGoal().hasSameLetter(boxNode.getBox())).
                    collect(Collectors.toList());
            for (Node goalNode : goalNodesWithSameLetter) {
                if (!goalNode.equals(boxNode)) {
                    result += 10 * (graph.shortestPath(boxNode, goalNode).size() + 1);
                }
            }
        }


        return result;
    }

    public abstract int f(Graph n);

    @Override
    public int compare(Graph n1, Graph n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {
        public AStar(Graph initialState, HashMap<Character, Integer> priortyList) {
            super(initialState, priortyList);
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

        public WeightedAStar(Graph initialState, int W) {
            super(initialState);
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
        public Greedy(Graph initialState) {
            super(initialState);
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
