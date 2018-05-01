package searchclient.model.Elements;

public class Agent extends ColeredElement {
    public Agent(int x, int y, char letter, String color) {
        super(x, y, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

    @Override
    public Agent clone() {
        return new Agent(this.getX(), this.getY(), this.getLetter(), this.getColor());
    }
}
