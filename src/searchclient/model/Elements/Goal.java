package searchclient.model.Elements;

public class Goal extends ColeredElement {
    private Goal(char letter, String color) {
        super(letter, color);
    }

    public Goal(char letter) {
        super(letter, null);
    }
}
