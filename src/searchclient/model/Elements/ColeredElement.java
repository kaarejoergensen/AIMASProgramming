package searchclient.model.Elements;

import java.util.Objects;

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

    public boolean hasSameLetter(ColeredElement other) {
        return Character.toLowerCase(this.letter) == Character.toLowerCase(other.getLetter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColeredElement that = (ColeredElement) o;
        return letter == that.letter &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {

        return Objects.hash(letter, color);
    }
}
