package searchclient;

import searchclient.Command.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class State {
    private State parent;

    private int columns;
    private int rows;
    private int numberOfAgents;
    private Map<Character, String> colorMap;

    private boolean[][] walls;
    private char[][] boxes;
    private char[][] goals;
    private char[][] agents;

    private Command[] actions;

    private int g;

    private int _hash = 0;

    public State(State parent, int columns, int rows, int numberOfAgents, Map<Character, String> colormap,
                 boolean[][] walls, char[][] boxes, char[][] goals, char[][] agents) {
        this.parent = parent;
        this.columns = columns;
        this.rows = rows;
        this.numberOfAgents = numberOfAgents;
        actions = new Command[numberOfAgents];
        for (int i = 0; i < actions.length; i++) {
            actions[i] = new Command();
        }

        this.colorMap = colormap;

        this.walls = walls;
        this.goals = goals;
        this.boxes = boxes;
        this.agents = agents;
        if (parent == null) {
            this.g = 0;
        } else {
            this.g = parent.g() + 1;
        }
    }

    public int g() {
        return this.g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < columns - 1; col++) {
                char g = goals[row][col];
                char b = Character.toLowerCase(boxes[row][col]);
                if (g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<State> getExpandedStates() {
        ArrayList<State> expandedStates = new ArrayList<>(Command.EVERY.length);
        for (Command c : Command.EVERY) {
            for (int row = 1; row < rows - 1; row++) {
                for (int col = 1; col < columns - 1; col++) {
                    if (agents[row][col] > 0) {
                        char agent = agents[row][col];
                        int newAgentRow = row + Command.dirToRowChange(c.dir1);
                        int newAgentCol = col + Command.dirToColChange(c.dir1);
                        Command action = null;
                        int newBoxRow = -1, newBoxCol = -1, oldBoxRow = -1, oldBoxCol = -1;

                        if (c.actionType == Type.Move) {
                            if (this.cellIsFree(newAgentRow, newAgentCol, agent)) {
                                action = c;
                            }
                        } else if (c.actionType == Type.Push) {
                            if (this.boxAt(newAgentRow, newAgentCol) &&
                                    this.boxAndAgentSameColor(this.boxes[newAgentRow][newAgentCol], agent)) {
                                int boxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                                int boxCol = newAgentCol + Command.dirToColChange(c.dir2);
                                if (this.cellIsFree(boxRow, boxCol, agent)) {
                                    action = c;
                                    newBoxRow = boxRow;
                                    newBoxCol = boxCol;
                                    oldBoxRow = newAgentRow;
                                    oldBoxCol = newAgentCol;
                                }
                            }
                        } else if (c.actionType == Type.Pull) {
                            if (this.cellIsFree(newAgentRow, newAgentCol, agent)) {
                                int boxRow = row + Command.dirToRowChange(c.dir2);
                                int boxCol = col + Command.dirToColChange(c.dir2);
                                if (this.boxAt(boxRow, boxCol) &&
                                        this.boxAndAgentSameColor(this.boxes[boxRow][boxCol], agent)) {
                                    action = c;
                                    newBoxRow = row;
                                    newBoxCol = col;
                                    oldBoxRow = boxRow;
                                    oldBoxCol = boxCol;
                                }
                            }
                        }

                        if (action != null) {
                            State n = this.ChildState();
                            n.actions[Character.getNumericValue(agent)] = action;
                            n.agents[newAgentRow][newAgentCol] = agent;
                            n.agents[row][col] = 0;
                            if (newBoxCol != -1 && newBoxRow != -1) {
                                n.boxes[newBoxRow][newBoxCol] = this.boxes[oldBoxRow][oldBoxCol];
                                n.boxes[oldBoxRow][oldBoxCol] = 0;
                            }
                            expandedStates.add(n);
                        }
                    }
                }
            }
        }
        return expandedStates;
    }

    private boolean cellIsFree(int row, int col, char agent) {
        return !this.walls[row][col] && this.boxes[row][col] == 0 && (this.agents[row][col] == 0 || this.agents[row][col]==agent);
    }

    private boolean boxAt(int row, int col) {
        return this.boxes[row][col] > 0;
    }

    private boolean boxAndAgentSameColor(char box, char agent) {
        return box > 0 && agent > 0 &&
                getColor(box).equals(getColor(agent));
    }

    private State ChildState() {
        char[][] boxesCopy = new char[rows][columns];
        char[][] agentsCopy = new char[rows][columns];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(this.boxes[row], 0, boxesCopy[row], 0, columns);
            System.arraycopy(this.agents[row], 0, agentsCopy[row], 0, columns);
        }
        return new State(this, columns, rows, numberOfAgents, this.colorMap, walls, boxesCopy, goals, agentsCopy);
    }

    public LinkedList<State> extractPlan() {
        LinkedList<State> plan = new LinkedList<>();
        State n = this;
        while (!n.isInitialState()) {
            plan.addFirst(n);
            n = n.parent;
        }
        return plan;
    }

    @Override
    public int hashCode() {
        if (this._hash == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + colorMap.hashCode();
            result = prime * result + Arrays.deepHashCode(this.agents);
            result = prime * result + Arrays.deepHashCode(this.boxes);
            result = prime * result + Arrays.deepHashCode(this.goals);
            result = prime * result + Arrays.deepHashCode(this.walls);
            this._hash = result;
        }
        return this._hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (!this.colorMap.equals(other.colorMap))
            return false;
        if (!Arrays.deepEquals(this.agents, other.agents))
            return false;
        if (!Arrays.deepEquals(this.boxes, other.boxes))
            return false;
        if (!Arrays.deepEquals(this.goals, other.goals))
            return false;
        if (!Arrays.deepEquals(this.walls, other.walls))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            if (!this.walls[row][0]) {
                break;
            }
            for (int col = 0; col < columns; col++) {
                if (this.boxes[row][col] > 0) {
                    s.append(this.boxes[row][col]);
                } else if (this.goals[row][col] > 0) {
                    s.append(this.goals[row][col]);
                } else if (this.walls[row][col]) {
                    s.append("+");
                } else if (this.agents[row][col] > 0) {
                    s.append(this.agents[row][col]);
                } else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Command[] getActions() {
        return actions;
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public char[][] getBoxes() {
        return boxes;
    }

    public char[][] getGoals() {
        return goals;
    }

    public char[][] getAgents() {
        return agents;
    }

    public String getColor(char letter) {
        String color = colorMap.get(letter);
        return color != null ? color : "blue";
    }
}