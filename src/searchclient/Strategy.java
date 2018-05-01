package searchclient;

import searchclient.model.Graph;

import java.util.*;

public abstract class Strategy {
    private HashSet<Graph> explored;
    private final long startTime;

    public Strategy() {
        this.explored = new HashSet<>();
        this.startTime = System.currentTimeMillis();
    }

    public void addToExplored(Graph n) {
        this.explored.add(n);
    }

    public boolean isExplored(Graph n) {
        return this.explored.contains(n);
    }

    public int countExplored() {
        return this.explored.size();
    }

    public String searchStatus() {
        return String.format("#Explored: %,6d, #Frontier: %,6d, #Generated: %,6d, Time: %3.2f s \t%s", this.countExplored(), this.countFrontier(), this.countExplored()+this.countFrontier(), this.timeSpent(), Memory.stringRep());
    }

    public float timeSpent() {
        return (System.currentTimeMillis() - this.startTime) / 1000f;
    }

    public abstract Graph getAndRemoveLeaf();

    public abstract void addToFrontier(Graph n);

    public abstract boolean inFrontier(Graph n);

    public abstract int countFrontier();

    public abstract boolean frontierIsEmpty();

    @Override
    public abstract String toString();

    public static class StrategyBFS extends Strategy {
        private ArrayDeque<Graph> frontier;
        private HashSet<Graph> frontierSet;

        public StrategyBFS() {
            super();
            frontier = new ArrayDeque<>();
            frontierSet = new HashSet<>();
        }

        @Override
        public Graph getAndRemoveLeaf() {
            Graph n = frontier.pollFirst();
            frontierSet.remove(n);
            return n;
        }

        @Override
        public void addToFrontier(Graph n) {
            frontier.addLast(n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(Graph n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Breadth-first Search";
        }
    }

    public static class StrategyDFS extends Strategy {
        private LinkedList<Graph> frontier;
        private HashSet<Graph> frontierSet;

        public StrategyDFS() {
            super();
            frontier = new LinkedList<>();
            frontierSet = new HashSet<>();
        }

        @Override
        public Graph getAndRemoveLeaf() {
            Graph n = frontier.removeLast();
            frontierSet.remove(n);
            return n;
        }

        @Override
        public void addToFrontier(Graph n) {
            frontier.add(n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(Graph n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Depth-first Search";
        }
    }

    public static class StrategyBestFirst extends Strategy {
        private Heuristic heuristic;

        private PriorityQueue<Graph> frontier;
        private HashSet<Graph> frontierSet;

        public StrategyBestFirst(Heuristic h) {
            super();
            this.heuristic = h;
            frontier = new PriorityQueue<>(heuristic);
            frontierSet = new HashSet<>();
        }

        @Override
        public Graph getAndRemoveLeaf() {
            Graph n = frontier.poll();
            frontierSet.remove(n);
            return n;
        }

        @Override
        public void addToFrontier(Graph n) {
            frontier.add(n);
            frontierSet.add(n);
        }

        public int h(Graph n ) {
            return  heuristic.h(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(Graph n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Best-first Search using " + this.heuristic.toString();
        }
    }
}
