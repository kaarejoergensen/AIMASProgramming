package searchclient.model.Elements;

public class ColeredElement {
    private char letter;
    private String color;

    public ColeredElement(char letter, String color) {
        this.letter = letter;
        this.color = color;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
