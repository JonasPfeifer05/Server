package util;

public class Counter {
    private int current;

    public Counter(int current) {
        this.current = current;
    }

    public void dec() {
        current--;
    }

    public int getCurrent() {
        return current;
    }
}
