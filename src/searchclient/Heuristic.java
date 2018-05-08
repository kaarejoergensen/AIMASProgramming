package searchclient;

import searchclient.model.Graph;
import searchclient.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Heuristic implements Comparator<Graph> {

    public Heuristic() {
    }

    public int h(Graph graph) {

        if (graph.getH() != -1) {
            return graph.getH();
        }
        int result = 0;

        for (Node agentNode : graph.getPriorityAgents()) {
            List<Node> boxNodesWithSameColor = graph.getPriorityBoxNodes();
            for (Node boxNode : boxNodesWithSameColor) {
                result += graph.shortestPath(agentNode, boxNode, true,null).
                        orElse(new ArrayList<>(10)).size();
            }
        }
        for (Node boxNode : graph.getPriorityBoxNodes()) {
            List<Node> goalNodesWithSameLetter = graph.getPriorityGoalNodes();
            for (Node goalNode : goalNodesWithSameLetter) {
                if (!goalNode.equals(boxNode)) {
                    result += 2 * (graph.shortestPath(boxNode, goalNode, true,null).
                            orElse(new ArrayList<>(10)).size() + 1);
                }
            }
        }
        graph.setH(result);
        return result;
    }

    public abstract int f(Graph n);

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
