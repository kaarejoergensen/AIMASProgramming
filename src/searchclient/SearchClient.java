package searchclient;

import searchclient.Heuristic.AStar;
import searchclient.Heuristic.Greedy;
import searchclient.Heuristic.WeightedAStar;
import searchclient.Strategy.StrategyBFS;
import searchclient.Strategy.StrategyBestFirst;
import searchclient.Strategy.StrategyDFS;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.Goal;
import searchclient.model.Graph;
import searchclient.model.Node;
import searchclient.model.Priority;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


public class SearchClient {


    private Graph initialState;
    private PriorityQueue<Priority> priorityList = new PriorityQueue<>((o1, o2) -> o2.getPriority() - o1.getPriority());

    private SearchClient(BufferedReader serverMessages) throws Exception {
        int row = 0;
        int columns = 0;
        int rows = 0;

        List<String> strings = new LinkedList<>();
        Map<Character, String> colorMap = new HashMap<>();

        String line = serverMessages.readLine();
        while (!line.equals("")) {
            if (line.matches("^[a-z]+:\\s*[0-9A-Z](\\s*,\\s*[0-9A-Z])*\\s*$")) {
                line = line.replaceAll("\\s", "");
                String[] colorSplit = line.split(":");
                if (colorSplit.length != 2) {
                    System.err.println("Color line not formatted properly! " + line);
                    System.exit(1);
                }
                String color = colorSplit[0];
                String[] ids = colorSplit[1].split(",");
                for (String id : ids) {
                    colorMap.put(id.charAt(0), color);
                }
            } else {
                strings.add(line);
                if (line.length() > columns) {
                    columns = line.length();
                }
                rows++;
            }
            line = serverMessages.readLine();
        }

        Node[][] tiles = new Node[rows][columns];
        Map<String, Agent> agents = new HashMap<>();
        Map<String, Box> boxes = new HashMap<>();
        Map<String, Goal> goals = new HashMap<>();

        int count = 0;
        for (String string : strings) {
            for (int col = 0; col < string.length(); col++) {
                char chr = string.charAt(col);
                if (chr != '+') {
                    Node node = null;
                    if ('0' <= chr && chr <= '9') {
                        node = new Node(String.valueOf(count), col, row);
                        agents.put(node.getId(), new Agent(String.valueOf(count), chr, colorMap.get(chr)));
                    } else if ('A' <= chr && chr <= 'Z') {
                        node = new Node(String.valueOf(count), col, row);
                        boxes.put(node.getId(), new Box(String.valueOf(count), chr, colorMap.get(chr)));
                    } else if ('a' <= chr && chr <= 'z') {
                        node = new Node(String.valueOf(count), col, row);
                        goals.put(node.getId(), new Goal(String.valueOf(count), chr));
                    } else if (chr == ' ') {
                        node = new Node(String.valueOf(count), col, row);
                    } else {
                        System.err.println("Error, read invalid level character: " + (int) chr);
                        System.exit(1);
                    }
                    tiles[row][col] = node;
                    if (tiles[row - 1][col] != null) {
                        node.addEdge(tiles[row - 1][col].getId());
                        tiles[row - 1][col].addEdge(node.getId());
                    }
                    if (tiles[row][col - 1] != null) {
                        node.addEdge(tiles[row][col - 1].getId());
                        tiles[row][col - 1].addEdge(node.getId());
                    }
                    count++;
                }
            }
            row++;
        }

        Map<String, Node> nodes = Arrays.stream(tiles).flatMap(Arrays::stream).
                filter(Objects::nonNull).collect(Collectors.toMap(Node::getId, n -> n));
        this.initialState = new Graph(null, rows, columns, nodes, agents, boxes, goals);
        generatePriorityList(initialState);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Read level and create the initial state of the problem
        SearchClient client = new SearchClient(serverMessages);

        Strategy strategy;
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "-bfs":
                    strategy = new StrategyBFS();
                    break;
                case "-dfs":
                    strategy = new StrategyDFS();
                    break;
                case "-astar":
                    strategy = new StrategyBestFirst(new AStar());
                    break;
                case "-wastar":
                    strategy = new StrategyBestFirst(new WeightedAStar(5));
                    break;
                case "-greedy":
                    strategy = new StrategyBestFirst(new Greedy());
                    break;
                default:
                    strategy = new StrategyBestFirst(new AStar());
                    System.err.println("Defaulting to astar search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to set the search strategy.");
            }
        } else {
            strategy = new StrategyBestFirst(new AStar());
            System.err.println("Defaulting to astar search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to set the search strategy.");
        }

        List<Graph> solution;
        try {
            solution = client.Search(strategy);
        } catch (OutOfMemoryError ex) {
            System.err.println("Maximum memory usage exceeded.");
            solution = null;
        }

        if (solution == null) {
            System.err.println(strategy.searchStatus());
            System.err.println("Unable to solve level.");
            System.exit(0);
        } else {
            System.err.println("\nSummary for " + strategy.toString());
            System.err.println("Found solution of length " + solution.size());
            System.err.println(strategy.searchStatus());
            for (Graph n : solution) {
                String act = n.actionsToString();
                System.out.println(act);
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n.toString());
                    break;
                }
            }
        }
    }

    private List<Graph> Search(Strategy strategy) throws Exception {
        System.err.format("Search starting with strategy %s.\n", strategy.toString());

        List<Graph> fullPlan = new LinkedList<>();
        fullPlan.add(initialState);

        int iterations = 0;
        while (!priorityList.isEmpty()) {
            Priority p = priorityList.poll();

            //Also calls the intantiate priority nodes
            fullPlan.get(fullPlan.size() - 1).setPriority(p);

            strategy.addToFrontier(fullPlan.get(fullPlan.size() - 1));


            while (true) {
                if (iterations == 1000) {
                    System.err.println(strategy.searchStatus());
                    iterations = 0;
                }

                if (strategy.frontierIsEmpty()) {
                    return null;
                }

                Graph leafState = strategy.getAndRemoveLeaf();

                if (leafState.isSubGoalState()) {
                    if (priorityList.isEmpty()) return leafState.extractPlan();
                    fullPlan.addAll(leafState.extractPlan());
                    break;
                }

                /*System.err.println(leafState.actionsToString());
                System.err.println(((StrategyBestFirst)strategy).h(leafState));*/
                //System.err.println(leafState);
                //Thread.sleep(1000);

                strategy.addToExplored(leafState);
                for (Graph n : leafState.getExpandedStates()) {
                    if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                        ((StrategyBestFirst) strategy).h(n);
                        strategy.addToFrontier(n);
                    }
                }
                iterations++;
            }
            strategy.clearFrontier();
        }

        return fullPlan;

    }

    private void generatePriorityList(Graph graph) {
        //Går utfra at det kun er 1 boks pr mål, og kun 1 mål pr char
        Map<Character, Integer> priorityMap = new HashMap<>();
        for (Node goalNode : graph.getGoalNodes()) {
            //Initates a new value to the hashmap
            priorityMap.put(graph.getGoal(goalNode).getLetter(), 0);
            //Finds all the goals between g and corresponding boxes
            for (Node boxNode : graph.getBoxNodes()) {
                if (Character.toLowerCase(graph.getBox(boxNode).getLetter()) == graph.getGoal(goalNode).getLetter()) {
                    List<Node> path = graph.shortestPath(boxNode, goalNode, false, null)
                            .orElse(graph.shortestPath(boxNode, goalNode, true, null).
                                    orElse(new ArrayList<>()));
                    //Counts the amount of goals on the way
                    for (Node pathNode : path) {
                        if (graph.getGoal(pathNode) != null) {
                            //Add value to tmp
                            priorityMap.put(graph.getGoal(goalNode).getLetter(), priorityMap.get(graph.getGoal(goalNode).getLetter()) + 1);
                        }
                    }
                }
            }
        }

        List<Priority> priorities = new ArrayList<>();
        for (Character key : priorityMap.keySet()) {
            Integer priority = priorityMap.get(key);
            Optional<Priority> optionalPriority = priorities.stream().filter(p -> p.getPriority() == priority).findFirst();
            if (optionalPriority.isPresent()) {
                optionalPriority.get().getLetters().add(key);
            } else {
                priorities.add(new Priority(new ArrayList<>(Collections.singletonList(key)), priority));
            }
        }
        priorityList.addAll(priorities);
    }
}
   /* public boolean getHelpFromAHomie(Graph state, List<Node> relevantAgents, List<Node> relevantBoxes, List<Node> relevantGoals){
        for(Node a : relevantAgents){
            for(Node b : relevantBoxes){
                Optional<List<Node>> path = state.shortestPath(a,b,false);
                if(path.isPresent()){
                    if(path.get().isEmpty()){
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }
        for(Node b : relevantBoxes){
            for(Node g : relevantGoals){
                Optional<List<Node>> path = state.shortestPath(b,g,false);
                if(path.isPresent()){
                    if(path.get().isEmpty()){
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }*/


