package searchclient.model;

import java.util.List;

public class Priority {
    private List<String> nodes_id;
    private int priority;


    public Priority(List<String> nodes_id, int priority) {
        this.nodes_id = nodes_id;
        this.priority = priority;
    }

    public List<String> getIDs() {
        return nodes_id;
    }

    public void setLetter(List<String> id) {
        this.nodes_id= id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "id=" + nodes_id +
                ", priority=" + priority +
                '}';
    }
}
