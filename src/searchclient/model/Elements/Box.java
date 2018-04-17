package searchclient.model.Elements;

public class Box extends ColeredElement {
    public Box(char letter, String color) {
        super(letter, color);
        if (this.getColor() == null) {
            this.setColor("blue");
        }
    }
}
