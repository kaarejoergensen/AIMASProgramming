package searchclient.model.Elements;

import java.util.Objects;

public class ColeredElement {
    private int x;
    private int y;
    private char letter;
    private String color;

    public ColeredElement(int x, int y, char letter, String color) {
        this.x = x;
        this.y = y;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPositionAsString() {
        return String.valueOf(x) + ',' + String.valueOf(y);
    }

    public boolean hasSameLetter(ColeredElement other) {
        return Character.toLowerCase(this.letter) == Character.toLowerCase(other.getLetter());
    }

    @Override
    public ColeredElement clone() {
        return new ColeredElement(this.x, this.y, this.letter, this.color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColeredElement that = (ColeredElement) o;
        return x == that.x &&
                y == that.y &&
                letter == that.letter &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, letter, color);
    }
}
