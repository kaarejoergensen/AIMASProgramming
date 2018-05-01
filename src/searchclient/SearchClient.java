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
        Node[][] tiles = new Node[rows][columns];
        Map<String, Agent> agents = new HashMap<>();
        Map<String, Box> boxes = new HashMap<>();
        Map<String, Goal> goals = new HashMap<>();

        for (String string : strings) {
            for (int col = 0; col < string.length(); col++) {
                char chr = string.charAt(col);
                if (chr != '+') {
                    Node node = null;
                    if ('0' <= chr && chr <= '9') {
                        node = new Node(col, row);
                        agents.put(node.getPositionAsString(), new Agent(col, row, chr, colorMap.get(chr)));
                    } else if ('A' <= chr && chr <= 'Z') {
                        node = new Node(col, row);
                        boxes.put(node.getPositionAsString(), new Box(col, row, chr, colorMap.get(chr)));
                    } else if ('a' <= chr && chr <= 'z') {
                        node = new Node(col, row);
                        goals.put(node.getPositionAsString(), new Goal(col, row, chr));
                    } else if (chr == ' ') {
                        node = new Node(col, row);
                    } else {
                        System.err.println("Error, read invalid level character: " + (int) chr);
                        System.exit(1);
                    }
                    tiles[row][col] = node;
                    if (tiles[row - 1][col] != null) {
                        node.addEdge(tiles[row - 1][col].getPositionAsString());
                        tiles[row - 1][col].addEdge(node.getPositionAsString());
                    }
                    if (tiles[row][col - 1] != null) {
                        node.addEdge(tiles[row][col - 1].getPositionAsString());
                        tiles[row][col - 1].addEdge(node.getPositionAsString());
                    }
                }
            }
            row++;
        }

        Map<String, Node> nodes = Arrays.stream(tiles).flatMap(Arrays::stream).
                filter(Objects::nonNull).collect(Collectors.toMap(Node::getPositionAsString, n -> n));
        this.initialState = new Graph(null, rows, columns, nodes, agents, boxes, goals);
        generatePriorityList(initialState);
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
                    strategy = new StrategyBestFirst(new AStar(null)); //#TODO: Real goal
                    break;
                case "-wastar":
                    // You're welcome to test WA* out with different values, but for the report you must at least indicate benchmarks for W = 5.
                    strategy = new StrategyBestFirst(new WeightedAStar(null, 5));  //#TODO: Real goal
                    break;
                case "-greedy":
                    strategy = new StrategyBestFirst(new Greedy(null));  //#TODO: Real goal
                    break;
                default:
                    strategy = new StrategyBFS();
                    System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to set the search strategy.");
            }
        } else {
            strategy = new StrategyBestFirst(new Greedy(null));  //#TODO: Real goal
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
                String act = n.actionsToString();
                System.out.println(act);
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n.toString());
                    System.err.format("%s was attempted in \n%s\n", n.getParent().actionsToString(), n.getParent().toString());
                    System.err.format("%s was attempted in \n%s\n", n.getParent().getParent().actionsToString(), n.getParent().getParent().toString());
                    break;
                }
            }
        }
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
//            System.out.println(((StrategyBestFirst) strategy).h(leafState));
//            try {
//                Thread.sleep(1500);
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

    public void generatePriorityList(Graph graph) {
        //Går utfra at det kun er 1 boks pr mål, og kun 1 mål pr char
        Map<Character, Integer> priorityMap = new HashMap<>();
        for (Node goalNode : graph.getGoalNodes()) {
            //Initates a new value to the hashmap
            priorityMap.put(graph.getGoal(goalNode).getLetter(), 0);
            //Finds all the goals between g and corresponding boxes
            for (Node boxNode : graph.getBoxNodes()) {
                if (Character.toLowerCase(graph.getBox(boxNode).getLetter()) == graph.getGoal(goalNode).getLetter()) {
                    List<Node> path = graph.shortestPath(boxNode, goalNode, false)
                            .orElse(graph.shortestPath(boxNode, goalNode, true).
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

        //Test
        System.err.println("Prio list bro: " + Arrays.asList(priorityList));
    }
}
