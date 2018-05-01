package searchclient.model;

import java.util.List;

public class Priority {
    private List<Character> letter;
    private int priority;

    public Priority(List<Character> letter, int priority) {
        this.letter = letter;
        this.priority = priority;
    }

    public List<Character> getLetters() {
        return letter;
    }

    public void setLetter(List<Character> letter) {
        this.letter = letter;
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
                "letter=" + letter +
                ", priority=" + priority +
                '}';
    }
}
