package searchclient;

import searchclient.model.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Heuristic implements Comparator<State> {
    public Heuristic(State n) {
    }

    public int h(State n) {
        int result = 0;

        List<Position> boxPositions = n.getBoxPositions();
        for(Position a : n.getAgentPositions()) {
            for (Position b : boxPositions) {
                if (n.getColor(n.getAgents()[a.row][a.col])
                        .equals(n.getColor(n.getBoxes()[b.row][b.col]))) {
                    result += 3 * n.getShortestPath(a, b, false)
                            .orElse(new ArrayList<>(10000)).size();
                }
            }
        }

        for(Position b : boxPositions) {
            for (Position g : n.getGoalPositions()) {
                if (n.getColor(n.getBoxes()[b.row][b.col]).equals(n.getColor(n.getGoals()[g.row][g.col]))) {
                    result += 10 * n.getShortestPath(b, g, false).
                            orElse(new ArrayList<>(10000)).size();
                }
            }
        }

        return result;
    }

    public abstract int f(State n);

    @Override
    public int compare(State n1, State n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {
        public AStar(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(State initialState, int W) {
            super(initialState);
            this.W = W;
        }

        @Override
        public int f(State n) {
            return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
            return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {
        public Greedy(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return this.h(n);
        }

        @Override
        public String toString() {
            return "Greedy evaluation";
        }
    }
}
