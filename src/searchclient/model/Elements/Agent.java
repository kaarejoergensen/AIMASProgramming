package searchclient.model.Elements;

public class Agent extends ColeredElement {
    public Agent(char letter, String color) {
        super(letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }
}
