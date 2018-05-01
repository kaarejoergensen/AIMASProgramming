package searchclient.model.Elements;

public class Goal extends ColeredElement {
    private Goal(int x, int y, char letter, String color) {
        super(x, y, letter, color);
    }

    public Goal(int x, int y, char letter) {
        super(x, y, letter, null);
    }

    @Override
    public Goal clone() {
        return new Goal(this.getX(), this.getY(), this.getLetter());
    }
}
