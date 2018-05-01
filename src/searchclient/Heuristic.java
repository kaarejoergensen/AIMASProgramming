package searchclient;

import searchclient.model.Elements.Goal;
import searchclient.model.Graph;
import searchclient.model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Heuristic implements Comparator<Graph> {

    private Goal currentGoal;

    public Heuristic(Goal currentGoal) {
        this.currentGoal = currentGoal;
    }

    public int h(Graph graph) {
        int result = 0;

        for (Node agentNode : graph.getAgentNodes()) {
            List<Node> boxNodesWithSameColor = graph.getBoxNodes().stream().
                    filter(n -> graph.getBox(n) != null && graph.getBox(n).getColor().equals(graph.getAgent(agentNode).getColor())).
                    collect(Collectors.toList());
            for (Node boxNode : boxNodesWithSameColor) {
                result += 3 * graph.shortestPath(agentNode, boxNode, false).
                        orElse(new ArrayList<>(10000)).size();
            }
        }
        boolean groupDone = true;
        for (Node boxNode : graph.getBoxNodes()) {
            List<Node> goalNodesWithSameLetter = graph.getGoalNodes().stream().
                    filter(n -> graph.getGoal(n).hasSameLetter(graph.getBox(boxNode))).
                    collect(Collectors.toList());
            for (Node goalNode : goalNodesWithSameLetter) {
                if (!goalNode.equals(boxNode)) {
                    groupDone = false;
                    result += 10 * (graph.shortestPath(boxNode, goalNode,false).
                            orElse(new ArrayList<>(10000)).size() + 1);
                }
            }
        }
        if (groupDone) {

        }

        return result;
    }

    public void setCurrentGoal(Goal newCurrentGoal) {
        this.currentGoal = newCurrentGoal;
    }

    public abstract int f(Graph n);

    @Override
    public int compare(Graph n1, Graph n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {

        public AStar(Goal currentGoal) {
            super(currentGoal);
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

        public WeightedAStar(Goal currentGoal, int W) {
            super(currentGoal);
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

        public Greedy(Goal currentGoal) {
            super(currentGoal);
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
