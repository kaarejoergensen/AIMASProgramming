package searchclient;

import java.util.*;

import searchclient.Heuristic.Location;

public abstract class Heuristic implements Comparator<State> {
    public Heuristic(State n) {
    }

    public int h(State n) {
        int result = 0;

        List<Location> boxes = new LinkedList<>();
        List<Location> goals = new LinkedList<>();
        List<Location> agents = new LinkedList<>();

        for (int row = 1; row < n.getRows() - 1; row ++) {
            for (int col = 1; col < n.getColumns() - 1; col++) {
                char g = n.getGoals()[row][col];
                char b = n.getBoxes()[row][col];
                char a = n.getAgents()[row][col];
                if (g > 0) {
                    goals.add(new Location(row, col, g, Integer.MAX_VALUE));
                }
                if (b > 0) {
                    boxes.add(new Location(row, col, b, Integer.MAX_VALUE));
                }
                if (a > 0) {
                    agents.add(new Location(row, col, a, Integer.MAX_VALUE));
                }
            }
        }

        for (Location goal : goals) {
            Iterator<Location> itr = boxes.iterator();
            while (itr.hasNext()) {
                Location box = itr.next();
                if (goal.character == Character.toLowerCase(box.character)) {
                    if (goal.x == box.x && goal.y == box.y) {
                        itr.remove();
                        break;
                    } else {
                        double distance = Math.sqrt(Math.pow(box.x - goal.x, 2) + Math.pow(box.y - goal.y, 2));
                        if (distance < box.distance) {
                            box.distance = (int) distance;
                        }
                    }
                }
            }
        }
        for (Location box : boxes) {
            result += 100 * box.distance;
            for (Location agent : agents) {
                if (n.getColor(box.character).equals(n.getColor(agent.character))) {
                    result += Math.abs((Math.sqrt(Math.pow(agent.x - box.x, 2) + Math.pow(agent.y - box.y, 2))) - 1);
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

    public class Location {
        public final int x;
        public final int y;
        public final char character;
        public int distance;

        public Location(int x, int y, char character, int distance) {
            this.x = x;
            this.y = y;
            this.character = character;
            this.distance = distance;
        }
    }
}
