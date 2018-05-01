package searchclient;

import searchclient.Heuristic.AStar;
import searchclient.Heuristic.Greedy;
import searchclient.Heuristic.WeightedAStar;
import searchclient.Strategy.StrategyBFS;
import searchclient.Strategy.StrategyBestFirst;
import searchclient.Strategy.StrategyDFS;
import searchclient.model.Edge;
import searchclient.model.Elements.Agent;
import searchclient.model.Elements.Box;
import searchclient.model.Elements.Goal;
import searchclient.model.Graph;
import searchclient.model.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


public class SearchClient {


    private Graph initialState;
    private HashMap<Character, Integer> priorityList;

    public SearchClient(BufferedReader serverMessages) throws Exception {
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
        /*boolean[][] walls = new boolean[rows][columns];
        char[][] boxes = new char[rows][columns];
        char[][] goals = new char[rows][columns];
        char[][] agents = new char[rows][columns];

        for (String string : strings) {
            for (int col = 0; col < string.length(); col++) {
                char chr = string.charAt(col);

                if (chr == '+') { // Wall.
                    walls[row][col] = true;
                } else if ('0' <= chr && chr <= '9') { // Agent.
                    agents[row][col] = chr;
                    numberOfAgents++;
                } else if ('A' <= chr && chr <= 'Z') { // Box.
                    boxes[row][col] = chr;
                } else if ('a' <= chr && chr <= 'z') { // Goal.
                    goals[row][col] = chr;
                } else if (chr != ' ') {
                    System.err.println("Error, read invalid level character: " + (int) chr);
                    System.exit(1);
                }
            }
            row++;
        }

        this.initialState = new State(null, columns, rows, numberOfAgents, colorMap, walls, boxes, goals, agents);*/

        Node[][] tiles = new Node[rows][columns];

        for (String string : strings) {
            for (int col = 0; col < string.length(); col++) {
                char chr = string.charAt(col);
                if (chr != '+') {
                    Node node = null;
                    if ('0' <= chr && chr <= '9') {
                        node = new Node(col, row, new Agent(chr, colorMap.get(chr)));
                    } else if ('A' <= chr && chr <= 'Z') {
                        node = new Node(col, row, new Box(chr, colorMap.get(chr)));
                    } else if ('a' <= chr && chr <= 'z') {
                        node = new Node(col, row, new Goal(chr));
                    } else if (chr == ' '){
                        node = new Node(col, row);
                    } else {
                        System.err.println("Error, read invalid level character: " + (int) chr);
                        System.exit(1);
                    }
                    tiles[row][col] = node;
                    if (tiles[row - 1][col] != null) {
                        node.addEdge(new Edge(node.getId(), tiles[row - 1][col].getId()));
                        tiles[row - 1][col].addEdge(new Edge(tiles[row - 1][col].getId(), node.getId()));
                    }
                    if (tiles[row][col - 1] != null) {
                        node.addEdge(new Edge(node.getId(), tiles[row][col - 1].getId()));
                        tiles[row][col - 1].addEdge(new Edge(tiles[row][col - 1].getId(), node.getId()));
                    }
                }
            }
            row++;
        }

        Map<String, Node> nodes = Arrays.stream(tiles).flatMap(Arrays::stream).
                filter(Objects::nonNull).collect(Collectors.toMap(Node::getId, n -> n));
        this.initialState = new Graph(null, rows, columns, nodes);
        generatePriorityList(initialState);
    }

    public List<Graph> Search(Strategy strategy) throws Exception {
        System.err.format("Search starting with strategy %s.\n", strategy.toString());
        strategy.addToFrontier(this.initialState);

        int iterations = 0;
        while (true) {
            if (iterations == 1000) {
                System.err.println(strategy.searchStatus());
                iterations = 0;
            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            Graph leafState = strategy.getAndRemoveLeaf();

            if (leafState.isGoalState()) {
                return leafState.extractPlan();
            }

//            System.out.println(leafState.actionsToString());
//            System.out.println(leafState);
//            System.out.println(((StrategyBestFirst)strategy).h(leafState));
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            strategy.addToExplored(leafState);
            for (Graph n : leafState.getExpandedStates()) { // The list of expanded States is shuffled randomly; see State.java.
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

    public void generatePriorityList(Graph graph){
        //Går utfra at det kun er 1 boks pr mål, og kun 1 mål pr char
        priorityList = new HashMap<>();
        for(Node g :  graph.getGoalNodes()){
            //Initates a new value to the hashmap
            priorityList.put(g.getGoal().getLetter(),0);
            //Finds all the goals between g and corresponding boxes
            for(Node b : graph.getBoxNodes()){
                if( Character.toLowerCase(b.getBox().getLetter()) == g.getGoal().getLetter()) {
                    List<Node> path = graph.shortestPath(b, g);
                    //Counts the amount of goals on the way
                    for (Node pathNode : path) {
                        if (pathNode.getGoal() != null) {
                            //Add value to tmp
                            priorityList.put(g.getGoal().getLetter(), priorityList.get(g.getGoal().getLetter()) + 1 );
                        }
                    }
                }
            }
        }
        //Test
        System.err.println("Prio list bro: " + Arrays.asList(priorityList));
    }

    public static void main(String[] args) throws Exception {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

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
                    strategy = new StrategyBestFirst(new AStar(client.initialState, client.priorityList));
                    break;
                case "-wastar":
                    // You're welcome to test WA* out with different values, but for the report you must at least indicate benchmarks for W = 5.
                    strategy = new StrategyBestFirst(new WeightedAStar(client.initialState, 5));
                    break;
                case "-greedy":
                    strategy = new StrategyBestFirst(new Greedy(client.initialState));
                    break;
                default:
                    strategy = new StrategyBFS();
                    System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to set the search strategy.");
            }
        } else {
            strategy = new StrategyBestFirst(new Greedy(client.initialState));
            System.err.println("Defaulting to greedy search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to set the search strategy.");
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
                StringBuilder act = new StringBuilder("[");
                for (Command cmd : n.getActions()) {
                    act.append(cmd).append(",");
                }
                act.setLength(act.length() - 1);
                act.append("]");
                System.out.println(act);
             }
        }
    }
}
