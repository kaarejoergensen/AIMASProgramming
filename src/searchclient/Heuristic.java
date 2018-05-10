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

            Node currentBox = graph.getAgentsCurrentBox(agentNode);
            result += graph.shortestPath(agentNode, currentBox, true, null).
                        orElse(new ArrayList<>(10)).size();

        }
        for (Node boxNode : graph.getPriorityBoxNodes()) {
            Node goal = graph.getDesignatedGoal(boxNode);
            if (!goal.equals(boxNode)) {
                result += 3 * (graph.shortestPath(boxNode, goal, true,null).
                        orElse(new ArrayList<>(10)).size() + 1);
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
