package searchclient.model.Elements;

public class Box extends ColeredElement {
    public Box(int x, int y, char letter, String color) {
        super(x, y, letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }

    @Override
    public Box clone() {
        return new Box(this.getX(), this.getY(), this.getLetter(), this.getColor());
    }
}
